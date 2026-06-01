package com.parkingapp.parkingappback.repositories;

import com.parkingapp.parkingappback.entities.ParkingSpot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParkingSpotRepository {
  List<ParkingSpot> findAll();

  Optional<ParkingSpot> findById(UUID id);

  ParkingSpot create(ParkingSpot parkingSpot);

  ParkingSpot update(ParkingSpot parkingSpot);

  boolean deleteById(UUID id);

  boolean releaseAllByIds(List<UUID> ids);

  boolean existsById(UUID id);

  boolean existsBySpotNumber(String spotNumber);
}
