package com.parkingapp.parkingappback.services;

import com.parkingapp.parkingappback.entities.Vehicle;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
  List<Vehicle> getAllVehicles(String licensePlate);

  Vehicle getVehicleById(UUID vehicleId);

  Vehicle createVehicle(String licensePlate, String brand, String model, UUID ownerId);

  Vehicle updateVehicle(UUID vehicleId, String licensePlate, String brand, String model, UUID ownerId);

  boolean deleteVehicle(UUID vehicleId);
}
