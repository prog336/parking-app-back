package com.parkingapp.parkingappback.exceptions.parkingSpots;

public class DuplicateSpotNumberException extends RuntimeException {
  public DuplicateSpotNumberException(String parkingSpotNumber) {
    super("Parking spot with this spot number already exists: " + parkingSpotNumber);
  }
}
