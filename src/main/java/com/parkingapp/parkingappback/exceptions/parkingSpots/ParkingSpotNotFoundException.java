package com.parkingapp.parkingappback.exceptions.parkingSpots;

import java.util.UUID;

public class ParkingSpotNotFoundException extends RuntimeException {
  public ParkingSpotNotFoundException(UUID parkingSpotId) {
    super("Parking spot not found with id: " + parkingSpotId);
  }
}
