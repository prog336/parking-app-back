package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.entities.Vehicle;
import com.parkingapp.parkingappback.exceptions.ValidationException;
import com.parkingapp.parkingappback.exceptions.vehicles.DuplicateLicensePlateException;
import com.parkingapp.parkingappback.exceptions.vehicles.VehicleNotFoundException;
import com.parkingapp.parkingappback.repositories.VehicleRepository;
import com.parkingapp.parkingappback.services.OwnerService;
import com.parkingapp.parkingappback.services.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VehicleServiceImpl implements VehicleService {
  private final VehicleRepository vehicleRepository;
  private final OwnerService ownerService;

  @Override
  public List<Vehicle> getAllVehicles(String licensePlate){
    if (licensePlate == null || licensePlate.isBlank()) return vehicleRepository.findAll();
    return  vehicleRepository.findByLicensePlate(licensePlate);
  }

  @Override
  public Vehicle getVehicleById(UUID vehicleId){
    return vehicleRepository.findById(vehicleId)
      .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
  }

  @Override
  public Vehicle createVehicle(String licensePlate, String brand, String model, UUID ownerId){
    validateVehicleData(licensePlate);
    if (vehicleRepository.existsByLicensePlate(licensePlate.toUpperCase())){
      throw new DuplicateLicensePlateException(licensePlate);
    }
    if (brand == null) brand = "";
    if (model == null) model = "";

    Owner owner = ownerService.getOwnerById(ownerId);
    Vehicle vehicle = new Vehicle();

    do vehicle.setId(UUID.randomUUID());
    while (vehicleRepository.existsById(vehicle.getId()));

    vehicle.setLicensePlate(licensePlate.toUpperCase());
    vehicle.setBrand(brand);
    vehicle.setModel(model);
    vehicle.setOwner(owner);

    return vehicleRepository.create(vehicle);
  }

  @Override
  public Vehicle updateVehicle(UUID vehicleId, String licensePlate, String brand, String model, UUID ownerId){
    validateVehicleData(licensePlate);
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
      .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

    if (!vehicle.getLicensePlate().equals(licensePlate.toUpperCase())
      && vehicleRepository.existsByLicensePlate(licensePlate.toUpperCase())){
      throw new DuplicateLicensePlateException(licensePlate);
    }
    if (brand == null) brand = "";
    if (model == null) model = "";

    Owner owner = ownerService.getOwnerById(ownerId);
    vehicle.setLicensePlate(licensePlate.toUpperCase());
    vehicle.setBrand(brand);
    vehicle.setModel(model);
    vehicle.setOwner(owner);

    return vehicleRepository.update(vehicle);
  }

  @Override
  public boolean deleteVehicle(UUID vehicleId){
    if (!vehicleRepository.existsById(vehicleId)){
      throw new VehicleNotFoundException(vehicleId);
    }

    return vehicleRepository.deleteById(vehicleId);
  }

  private void validateVehicleData(String licensePlate){
    if (licensePlate == null || licensePlate.isBlank()) {
      throw new ValidationException("License plate should not be empty");
    }
  }
}
