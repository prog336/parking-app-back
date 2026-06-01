package com.parkingapp.parkingappback.repositories;

import com.parkingapp.parkingappback.entities.Owner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository {
  List<Owner> findAll();

  Optional<Owner> findById(UUID id);

  List<Owner> findByFullName(String fullName);

  Owner create(Owner owner);

  Owner update(Owner owner);

  boolean deleteById(UUID id);

  boolean existsById(UUID id);

  boolean existsByPhoneNumber(String phoneNumber);
}
