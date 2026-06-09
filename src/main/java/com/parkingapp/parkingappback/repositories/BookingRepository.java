package com.parkingapp.parkingappback.repositories;

import com.parkingapp.parkingappback.entities.Booking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
  List<Booking> findAll();

  Optional<Booking> findById(UUID id);

  List<Booking> findByVehicleLicensePlate(String licensePlate);

  List<Booking> findByOwnerFullName(String fullName);

  List<Booking> findByVehicleLicensePlateAndOwnerFullName(String licensePlate, String fullName);

  Booking create(Booking booking);

  Booking update(Booking booking);

  boolean deleteById(UUID id);

  boolean deleteExpired();

  boolean existsById(UUID id);

  boolean existsByParkingSpotIdOrVehicleId(UUID parkingSpotId, UUID vehicleId);
}
