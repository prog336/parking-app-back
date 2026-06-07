package com.parkingapp.parkingappback.exceptions.bookings;

import java.util.UUID;

public class DuplicateVehicleOrParkingSpotException extends RuntimeException {
  public DuplicateVehicleOrParkingSpotException(UUID vehicleId, UUID parkingSpotId) {
    super("Booking for this vehicle or parking spot already exists: " + vehicleId + ", " + parkingSpotId);
  }
}
