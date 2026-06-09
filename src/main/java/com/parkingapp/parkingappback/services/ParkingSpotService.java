package com.parkingapp.parkingappback.services;

import com.parkingapp.parkingappback.entities.ParkingSpot;

import java.util.List;
import java.util.UUID;

public interface ParkingSpotService {
  List<ParkingSpot> getAllParkingSpots();

  ParkingSpot getParkingSpotById(UUID parkingSpotId);

  ParkingSpot createParkingSpot(String spotNumber);

  ParkingSpot updateParkingSpotNumber(UUID parkingSpotId, String spotNumber);

  ParkingSpot updateParkingSpotOccupation(UUID parkingSpotId, boolean isOccupied);

  boolean deleteParkingSpot(UUID vehicleId);
}
