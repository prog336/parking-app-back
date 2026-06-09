package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.entities.Vehicle;
import com.parkingapp.parkingappback.events.OwnerChangedEvent;
import com.parkingapp.parkingappback.events.OwnerDeletedEvent;
import com.parkingapp.parkingappback.events.VehicleChangedEvent;
import com.parkingapp.parkingappback.events.VehicleDeletedEvent;
import com.parkingapp.parkingappback.exceptions.ValidationException;
import com.parkingapp.parkingappback.exceptions.vehicles.DuplicateLicensePlateException;
import com.parkingapp.parkingappback.exceptions.vehicles.VehicleNotFoundException;
import com.parkingapp.parkingappback.repositories.VehicleRepository;
import com.parkingapp.parkingappback.services.OwnerService;
import com.parkingapp.parkingappback.services.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VehicleServiceImpl implements VehicleService {
  private final VehicleRepository vehicleRepository;
  private final OwnerService ownerService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Cacheable(value = "vehicles")
  public List<Vehicle> getAllVehicles(String licensePlate){
    if (licensePlate == null || licensePlate.isBlank()) return vehicleRepository.findAll();
    return  vehicleRepository.findByLicensePlate(licensePlate);
  }

  @Override
  @Cacheable(value = "vehicleById", key = "#vehicleId")
  public Vehicle getVehicleById(UUID vehicleId){
    return vehicleRepository.findById(vehicleId)
      .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "vehicles", allEntries = true),
    @CacheEvict(value = "vehicleById", key = "#result.id")
  })
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
  @Caching(evict = {
    @CacheEvict(value = "vehicles", allEntries = true),
    @CacheEvict(value = "vehicleById", key = "#vehicleId")
  })
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
    Vehicle updatedVehicle = vehicleRepository.update(vehicle);
    eventPublisher.publishEvent(new VehicleChangedEvent(this));

    return updatedVehicle;
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "vehicles", allEntries = true),
    @CacheEvict(value = "vehicleById", key = "#vehicleId")
  })
  public boolean deleteVehicle(UUID vehicleId){
    if (!vehicleRepository.existsById(vehicleId)){
      throw new VehicleNotFoundException(vehicleId);
    }

    boolean deleted = vehicleRepository.deleteById(vehicleId);
    if (deleted){
      eventPublisher.publishEvent(new VehicleDeletedEvent(this));
    }

    return deleted;
  }

  @EventListener
  @Caching(evict = {
    @CacheEvict(value = "vehicles", allEntries = true),
    @CacheEvict(value = "vehicleById", allEntries = true)
  })
  public void evictVehicleCache(OwnerChangedEvent event){
    eventPublisher.publishEvent(new VehicleChangedEvent(this));
  }

  @EventListener
  @Caching(evict = {
    @CacheEvict(value = "vehicles", allEntries = true),
    @CacheEvict(value = "vehicleById", allEntries = true)
  })
  public void evictVehicleCache(OwnerDeletedEvent event){
    eventPublisher.publishEvent(new VehicleDeletedEvent(this));
  }

  private void validateVehicleData(String licensePlate){
    if (licensePlate == null || licensePlate.isBlank()) {
      throw new ValidationException("License plate should not be empty");
    }
  }
}
