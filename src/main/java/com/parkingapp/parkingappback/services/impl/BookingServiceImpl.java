package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.Booking;
import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.entities.Vehicle;
import com.parkingapp.parkingappback.events.BookingDeletedEvent;
import com.parkingapp.parkingappback.events.ParkingSpotChangedEvent;
import com.parkingapp.parkingappback.events.VehicleChangedEvent;
import com.parkingapp.parkingappback.events.VehicleDeletedEvent;
import com.parkingapp.parkingappback.exceptions.ValidationException;
import com.parkingapp.parkingappback.exceptions.bookings.BookingNotFoundException;
import com.parkingapp.parkingappback.exceptions.bookings.DuplicateVehicleOrParkingSpotException;
import com.parkingapp.parkingappback.repositories.BookingRepository;
import com.parkingapp.parkingappback.services.BookingService;
import com.parkingapp.parkingappback.services.ParkingSpotService;
import com.parkingapp.parkingappback.services.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
  private final BookingRepository bookingRepository;
  private final ParkingSpotService parkingSpotService;
  private final VehicleService vehicleService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Cacheable(value = "bookings")
  public List<Booking> getAllBookings(String licensePlate, String fullName){
    if (licensePlate != null && !licensePlate.isBlank() && fullName != null && !fullName.isBlank()){
      return bookingRepository.findByVehicleLicensePlateAndOwnerFullName(licensePlate, fullName);
    }
    if (licensePlate != null && !licensePlate.isBlank()) return bookingRepository.findByVehicleLicensePlate(licensePlate);
    if (fullName != null && !fullName.isBlank()) return bookingRepository.findByOwnerFullName(fullName);
    return bookingRepository.findAll();
  }

  @Override
  @Transactional
  @CacheEvict(value = "bookings", allEntries = true)
  public Booking createBooking(UUID parkingSpotId, UUID vehicleId, Instant startTime, Instant endTime){
    if (startTime == null || endTime == null || parkingSpotId == null || vehicleId == null){
      throw new ValidationException("Start time, end time, spot id and vehicle id should not be empty");
    }
    if (endTime.isBefore(Instant.now())){
      throw new ValidationException("End time cannot be earlier than now");
    }
    if (endTime.isBefore(startTime)){
      throw new ValidationException("End time cannot be earlier than start time");
    }
    if (bookingRepository.existsByParkingSpotIdOrVehicleId(parkingSpotId, vehicleId)){
      throw new DuplicateVehicleOrParkingSpotException(vehicleId, parkingSpotId);
    }

    ParkingSpot parkingSpot = parkingSpotService.getParkingSpotById(parkingSpotId);
    parkingSpotService.updateParkingSpotOccupation(parkingSpotId, true);
    Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
    Booking booking = new Booking();

    do booking.setId(UUID.randomUUID());
    while (bookingRepository.existsById(booking.getId()));

    booking.setStartTime(startTime);
    booking.setEndTime(endTime);
    booking.setPaid(false);
    booking.setCost((int) (100 + 100 * (Duration.between(startTime, endTime).toHours())));
    if (booking.getCost() <= 0) booking.setCost(100);
    booking.setParkingSpot(parkingSpot);
    booking.setVehicle(vehicle);


    return bookingRepository.create(booking);
  }

  @Override
  @CacheEvict(value = "bookings", allEntries = true)
  public Booking payBooking(UUID bookingId){
    Booking booking = bookingRepository.findById(bookingId)
      .orElseThrow(() -> new BookingNotFoundException(bookingId));

    if (booking.isPaid()) return booking;

    booking.setPaid(true);

    return bookingRepository.update(booking);
  }

  @Override
  @Transactional
  @CacheEvict(value = "bookings", allEntries = true)
  public boolean deleteBooking(UUID bookingId){
    boolean deleted = bookingRepository.deleteById(bookingId);
    if (deleted){
      eventPublisher.publishEvent(new BookingDeletedEvent(this));
    }

    return deleted;
  }

  @Override
  @Transactional
  @CacheEvict(value = "bookings", allEntries = true)
  public boolean deleteExpiredBookings(){
    boolean deleted = bookingRepository.deleteExpired();
    if (deleted){
      eventPublisher.publishEvent(new BookingDeletedEvent(this));
    }

    return deleted;
  }

  @EventListener
  @CacheEvict(value = "bookings", allEntries = true)
  public void evictBookingsCache(ParkingSpotChangedEvent event){}

  @EventListener
  @CacheEvict(value = "bookings", allEntries = true)
  public void evictBookingsCache(VehicleChangedEvent event){}

  @EventListener
  @CacheEvict(value = "bookings", allEntries = true)
  public void evictBookingsCache(VehicleDeletedEvent event){
    eventPublisher.publishEvent(new BookingDeletedEvent(this));
  }
}
