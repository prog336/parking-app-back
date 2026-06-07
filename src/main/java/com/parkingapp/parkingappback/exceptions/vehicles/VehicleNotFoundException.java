package com.parkingapp.parkingappback.exceptions.vehicles;

import java.util.UUID;

public class VehicleNotFoundException extends RuntimeException {
  public VehicleNotFoundException(UUID vehicleId) {
    super("Vehicle not found with id: " + vehicleId);
  }
}
