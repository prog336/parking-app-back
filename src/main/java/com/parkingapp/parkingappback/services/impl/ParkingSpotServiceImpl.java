package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.exceptions.parkingSpots.DuplicateSpotNumberException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.ParkingSpotAlreadyOccupiedException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.ParkingSpotNotFoundException;
import com.parkingapp.parkingappback.repositories.ParkingSpotRepository;
import com.parkingapp.parkingappback.services.ParkingSpotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ParkingSpotServiceImpl implements ParkingSpotService {
  private final ParkingSpotRepository parkingSpotRepository;

  @Override
  public List<ParkingSpot> getAllParkingSpots(){
    return parkingSpotRepository.findAll();
  }

  @Override
  public ParkingSpot getParkingSpotById(UUID parkingSpotId){
    return parkingSpotRepository.findById(parkingSpotId)
      .orElseThrow(() -> new ParkingSpotNotFoundException(parkingSpotId));
  }

  @Override
  public ParkingSpot createParkingSpot(String spotNumber){
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
  public ParkingSpot updateParkingSpotNumber(UUID parkingSpotId, String spotNumber){
    ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
      .orElseThrow(() -> new ParkingSpotNotFoundException(parkingSpotId));

    if (!parkingSpot.getSpotNumber().equals(spotNumber) && parkingSpotRepository.existsBySpotNumber(spotNumber)){
      throw new DuplicateSpotNumberException(spotNumber);
    }

    parkingSpot.setSpotNumber(spotNumber);

    return parkingSpotRepository.update(parkingSpot);
  }

  @Override
  public ParkingSpot updateParkingSpotOccupation(UUID parkingSpotId, boolean isOccupied){
    ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
      .orElseThrow(() -> new ParkingSpotNotFoundException(parkingSpotId));

    if (parkingSpot.isOccupied() && isOccupied){
      throw new ParkingSpotAlreadyOccupiedException(parkingSpotId);
    }

    parkingSpot.setOccupied(isOccupied);

    return parkingSpotRepository.update(parkingSpot);
  }

  @Override
  @Transactional
  public boolean releaseParkingSpots(List<UUID> parkingSpotIds){
    return parkingSpotRepository.releaseAllByIds(parkingSpotIds);
  }

  @Override
  public boolean deleteParkingSpot(UUID parkingSpotId){
    if (!parkingSpotRepository.existsById(parkingSpotId)){
      throw new ParkingSpotNotFoundException(parkingSpotId);
    }

    return parkingSpotRepository.deleteById(parkingSpotId);
  }
}
