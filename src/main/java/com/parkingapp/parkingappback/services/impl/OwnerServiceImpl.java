package com.parkingapp.parkingappback.services.impl;

import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.events.OwnerChangedEvent;
import com.parkingapp.parkingappback.events.OwnerDeletedEvent;
import com.parkingapp.parkingappback.exceptions.ValidationException;
import com.parkingapp.parkingappback.exceptions.owners.DuplicatePhoneNumberException;
import com.parkingapp.parkingappback.exceptions.owners.OwnerNotFoundException;
import com.parkingapp.parkingappback.repositories.OwnerRepository;
import com.parkingapp.parkingappback.services.OwnerService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OwnerServiceImpl implements OwnerService {
  private final OwnerRepository ownerRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Cacheable(value = "owners")
  public List<Owner> getAllOwners(String fullName){
    if (fullName == null || fullName.isBlank()) return ownerRepository.findAll();
    return ownerRepository.findByFullName(fullName);
  }

  @Override
  @Cacheable(value = "ownerById", key = "#ownerId")
  public Owner getOwnerById(UUID ownerId){
    return ownerRepository.findById(ownerId)
      .orElseThrow(() -> new OwnerNotFoundException(ownerId));
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "owners", allEntries = true),
    @CacheEvict(value = "ownerById", key = "#result.id")
  })
  public Owner createOwner(String fullName, String phoneNumber){
    String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
    validateOwnerData(fullName, formattedPhoneNumber);

    if (ownerRepository.existsByPhoneNumber(formattedPhoneNumber)){
      throw new DuplicatePhoneNumberException(formattedPhoneNumber);
    }

    Owner owner = new Owner();

    do owner.setId(UUID.randomUUID());
    while (ownerRepository.existsById(owner.getId()));

    owner.setFullName(fullName);
    owner.setPhoneNumber(formattedPhoneNumber);

    return ownerRepository.create(owner);
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "owners", allEntries = true),
    @CacheEvict(value = "ownerById", key = "#ownerId")
  })
  public Owner updateOwner(UUID ownerId, String fullName, String phoneNumber){
    String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
    validateOwnerData(fullName, formattedPhoneNumber);

    Owner owner = ownerRepository.findById(ownerId)
      .orElseThrow(() -> new OwnerNotFoundException(ownerId));

    if (!owner.getPhoneNumber().equals(formattedPhoneNumber) && ownerRepository.existsByPhoneNumber(formattedPhoneNumber)){
      throw new DuplicatePhoneNumberException(formattedPhoneNumber);
    }

    owner.setFullName(fullName);
    owner.setPhoneNumber(formattedPhoneNumber);
    Owner updatedOwner = ownerRepository.update(owner);
    eventPublisher.publishEvent(new OwnerChangedEvent(this));

    return updatedOwner;
  }

  @Override
  @Caching(evict = {
    @CacheEvict(value = "owners", allEntries = true),
    @CacheEvict(value = "ownerById", key = "#ownerId"),
  })
  public boolean deleteOwner(UUID ownerId){
    if (!ownerRepository.existsById(ownerId)){
      throw new OwnerNotFoundException(ownerId);
    }

    boolean deleted = ownerRepository.deleteById(ownerId);
    if (deleted){
      eventPublisher.publishEvent(new OwnerDeletedEvent(this));
    }

    return deleted;
  }

  private String formatPhoneNumber(String phoneNumber){
    String formattedPhoneNumber = phoneNumber.replaceAll("[^\\d+]", "");

    if (formattedPhoneNumber.startsWith("8")){
      formattedPhoneNumber = formattedPhoneNumber.replaceFirst("8", "+7");
    }

    return formattedPhoneNumber;
  }

  private void validateOwnerData(String fullName, String phoneNumber){
    if (fullName == null || phoneNumber == null || fullName.isBlank() || phoneNumber.isBlank()) {
      throw new  ValidationException("Full name and phone should not be empty");
    }
    if (fullName.length() < 2) {
      throw new  ValidationException("Full name should be at least 2 characters long");
    }
    String phoneRegex = "^(\\+7|8)?\\d{10}$";
    if (!phoneNumber.matches(phoneRegex)) {
      throw new  ValidationException("Wrong phone number format");
    }
  }
}
