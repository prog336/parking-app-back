package com.parkingapp.parkingappback.exceptions.parkingSpots;

import java.util.UUID;

public class ParkingSpotAlreadyOccupiedException extends RuntimeException {
  public ParkingSpotAlreadyOccupiedException(UUID parkingSpotId) {
    super("Parking spot already occupied: " + parkingSpotId);
  }
}
