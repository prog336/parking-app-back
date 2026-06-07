package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.Booking;
import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.entities.Vehicle;
import com.parkingapp.parkingappback.exceptions.bookings.BookingNotFoundException;
import com.parkingapp.parkingappback.exceptions.bookings.DuplicateVehicleOrParkingSpotException;
import com.parkingapp.parkingappback.repositories.BookingRepository;
import com.parkingapp.parkingappback.services.BookingService;
import com.parkingapp.parkingappback.services.ParkingSpotService;
import com.parkingapp.parkingappback.services.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
  private final BookingRepository bookingRepository;
  private final ParkingSpotService parkingSpotService;
  private final VehicleService vehicleService;

  @Override
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
  public Booking createBooking(UUID parkingSpotId, UUID vehicleId, Instant startTime, Instant endTime){
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
  public Booking payBooking(UUID bookingId){
    Booking booking = bookingRepository.findById(bookingId)
      .orElseThrow(() -> new BookingNotFoundException(bookingId));

    if (booking.isPaid()) return booking;

    booking.setPaid(true);

    return bookingRepository.update(booking);
  }

  @Override
  @Transactional
  public boolean deleteBooking(UUID bookingId){
    Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

    if (optionalBooking.isEmpty()){
      throw new BookingNotFoundException(bookingId);
    }

    Booking booking = optionalBooking.get();
    parkingSpotService.updateParkingSpotOccupation(booking.getParkingSpot().getId(), false);

    return bookingRepository.deleteById(bookingId);
  }

  @Transactional
  @Override
  public boolean deleteExpiredBookings(){
    List<Booking> bookings = bookingRepository.findByEndTime(Instant.now());

    if (bookings == null || bookings.isEmpty()){
      return true;
    }

    List<UUID> parkingSpotsIds = bookings.stream().map(booking -> {
      ParkingSpot parkingSpot = booking.getParkingSpot();
      return parkingSpot.getId();
    }).toList();

    if (!parkingSpotService.releaseParkingSpots(parkingSpotsIds)){
      return false;
    }

    List<UUID> bookingIds = bookings.stream().map(Booking::getId).toList();

    return bookingRepository.deleteAllByIds(bookingIds);
  }
}
