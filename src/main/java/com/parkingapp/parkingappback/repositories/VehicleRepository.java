package com.parkingapp.parkingappback.repositories;

import com.parkingapp.parkingappback.entities.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {
  List<Vehicle> findAll();

  Optional<Vehicle> findById(UUID id);

  List<Vehicle> findByLicensePlate(String licensePlate);

  Vehicle create(Vehicle vehicle);

  Vehicle update(Vehicle vehicle);

  boolean deleteById(UUID id);

  boolean existsById(UUID id);

  boolean existsByLicensePlate(String licensePlate);
}
