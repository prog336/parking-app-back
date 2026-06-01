package com.parkingapp.parkingappback.services;

import com.parkingapp.parkingappback.entities.Owner;

import java.util.List;
import java.util.UUID;

public interface OwnerService {
  List<Owner> getAllOwners(String fullName);

  Owner getOwnerById(UUID ownerId);

  Owner createOwner(String fullName, String phoneNumber);

  Owner updateOwner (UUID ownerId, String fullName, String phoneNumber);

  boolean deleteOwner(UUID ownerId);
}
