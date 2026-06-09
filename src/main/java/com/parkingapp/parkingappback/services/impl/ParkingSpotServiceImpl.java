package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.events.BookingDeletedEvent;
import com.parkingapp.parkingappback.events.ParkingSpotChangedEvent;
import com.parkingapp.parkingappback.exceptions.ValidationException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.DuplicateSpotNumberException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.ParkingSpotAlreadyOccupiedException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.ParkingSpotNotFoundException;
import com.parkingapp.parkingappback.repositories.ParkingSpotRepository;
import com.parkingapp.parkingappback.services.ParkingSpotService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {
  private final ParkingSpotRepository parkingSpotRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Cacheable(value = "parkingSpots")
  public List<ParkingSpot> getAllParkingSpots(){
    return parkingSpotRepository.findAll();
  }

  @Override
  @Cacheable(value = "parkingSpotById", key = "#parkingSpotId")
  public ParkingSpot getParkingSpotById(UUID parkingSpotId){
    return parkingSpotRepository.findById(parkingSpotId)
      .orElseThrow(() -> new ParkingSpotNotFoundException(parkingSpotId));
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "parkingSpots", allEntries = true),
    @CacheEvict(value = "parkingSpotById", key = "#result.id")
  })
  public ParkingSpot createParkingSpot(String spotNumber){
    validateParkingSpotData(spotNumber);

    if (parkingSpotRepository.existsBySpotNumber(spotNumber)){
      throw new DuplicateSpotNumberException(spotNumber);
    }

    ParkingSpot parkingSpot = new ParkingSpot();

    do parkingSpot.setId(UUID.randomUUID());
    while (parkingSpotRepository.existsById(parkingSpot.getId()));

    parkingSpot.setSpotNumber(spotNumber);
    parkingSpot.setOccupied(false);

    return parkingSpotRepository.create(parkingSpot);
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "parkingSpots", allEntries = true),
    @CacheEvict(value = "parkingSpotById", key = "#parkingSpotId")
  })
  public ParkingSpot updateParkingSpotNumber(UUID parkingSpotId, String spotNumber){
    validateParkingSpotData(spotNumber);
    ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
      .orElseThrow(() -> new ParkingSpotNotFoundException(parkingSpotId));

    if (!parkingSpot.getSpotNumber().equals(spotNumber) && parkingSpotRepository.existsBySpotNumber(spotNumber)){
      throw new DuplicateSpotNumberException(spotNumber);
    }

    parkingSpot.setSpotNumber(spotNumber);
    ParkingSpot updatedParkingSpot = parkingSpotRepository.update(parkingSpot);
    eventPublisher.publishEvent(new ParkingSpotChangedEvent(this));

    return updatedParkingSpot;
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "parkingSpots", allEntries = true),
    @CacheEvict(value = "parkingSpotById", key = "#parkingSpotId")
  })
  public ParkingSpot updateParkingSpotOccupation(UUID parkingSpotId, boolean isOccupied){
    ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
      .orElseThrow(() -> new ParkingSpotNotFoundException(parkingSpotId));

    if (parkingSpot.isOccupied() && isOccupied){
      throw new ParkingSpotAlreadyOccupiedException(parkingSpotId);
    }

    parkingSpot.setOccupied(isOccupied);
    ParkingSpot updatedParkingSpot = parkingSpotRepository.update(parkingSpot);
    eventPublisher.publishEvent(new ParkingSpotChangedEvent(this));

    return updatedParkingSpot;
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "parkingSpots", allEntries = true),
    @CacheEvict(value = "parkingSpotById", key = "#parkingSpotId")
  })
  public boolean deleteParkingSpot(UUID parkingSpotId){
    if (!parkingSpotRepository.existsById(parkingSpotId)){
      throw new ParkingSpotNotFoundException(parkingSpotId);
    }

    boolean deleted = parkingSpotRepository.deleteById(parkingSpotId);
    if (deleted) {
      eventPublisher.publishEvent(new ParkingSpotChangedEvent(this));
    }

    return deleted;
  }

  @EventListener
  @Caching(evict = {
    @CacheEvict(value = "parkingSpots", allEntries = true),
    @CacheEvict(value = "parkingSpotById", allEntries = true)
  })
  public void evictParkingSpotsCache(BookingDeletedEvent event){}

  private void validateParkingSpotData(String spotNumber){
    if (spotNumber == null || spotNumber.isBlank()){
      throw new ValidationException("Spot number should not be empty");
    }
  }
}
