package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.exceptions.owners.DuplicatePhoneNumberException;
import com.parkingapp.parkingappback.exceptions.owners.OwnerNotFoundException;
import com.parkingapp.parkingappback.repositories.OwnerRepository;
import com.parkingapp.parkingappback.services.OwnerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OwnerServiceImpl implements OwnerService {
  private final OwnerRepository ownerRepository;

  @Override
  public List<Owner> getAllOwners(String fullName){
    if (fullName == null || fullName.isBlank()) return ownerRepository.findAll();
    return ownerRepository.findByFullName(fullName);
  }

  @Override
  public Owner getOwnerById(UUID ownerId){
    return ownerRepository.findById(ownerId)
      .orElseThrow(() -> new OwnerNotFoundException(ownerId));
  }

  @Override
  public Owner createOwner(String fullName, String phoneNumber){
    if (ownerRepository.existsByPhoneNumber(phoneNumber)){
      throw new DuplicatePhoneNumberException(phoneNumber);
    }

    Owner owner = new Owner();

    do owner.setId(UUID.randomUUID());
    while (ownerRepository.existsById(owner.getId()));

    owner.setFullName(fullName);
    owner.setPhoneNumber(phoneNumber);

    return ownerRepository.create(owner);
  }

  @Override
  public Owner updateOwner(UUID ownerId, String fullName, String phoneNumber){
    Owner owner = ownerRepository.findById(ownerId)
      .orElseThrow(() -> new OwnerNotFoundException(ownerId));

    if (!owner.getPhoneNumber().equals(phoneNumber) && ownerRepository.existsByPhoneNumber(phoneNumber)){
      throw new DuplicatePhoneNumberException(phoneNumber);
    }

    owner.setFullName(fullName);
    owner.setPhoneNumber(phoneNumber);

    return ownerRepository.update(owner);
  }

  @Override
  public boolean deleteOwner(UUID ownerId){
    if (!ownerRepository.existsById(ownerId)){
      throw new OwnerNotFoundException(ownerId);
    }

    return ownerRepository.deleteById(ownerId);
  }
}
