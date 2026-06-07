package com.parkingapp.parkingappback.controllers;

import com.parkingapp.parkingappback.DTOs.request.OwnerCreateDTO;
import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.services.OwnerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/owners")
@AllArgsConstructor
@Slf4j
public class OwnerController {
  private final OwnerService ownerService;

  @GetMapping
  public ResponseEntity<List<Owner>> getAllOwners(@RequestParam(required = false) String fullName) {
    List<Owner> owners = ownerService.getAllOwners(fullName);

    return ResponseEntity.ok(owners);
  }

  @PostMapping
  public ResponseEntity<?> createOwner(@RequestBody OwnerCreateDTO ownerCreateDTO) {
    if (ownerCreateDTO.fullName() == null || ownerCreateDTO.phoneNumber() == null ||
      ownerCreateDTO.fullName().isBlank() || ownerCreateDTO.phoneNumber().isBlank()) {
      return ResponseEntity.badRequest().body("Full name and phone should not be empty");
    }
    if (ownerCreateDTO.fullName().length() < 2) {
      return ResponseEntity.badRequest().body("Full name should be at least 2 characters long");
    }
    String cleanedPhoneNumber = ownerCreateDTO.phoneNumber().replaceAll("[^\\d+]", "");
    String phoneRegex = "^(\\+7|8)?\\d{10}$";
    if (!cleanedPhoneNumber.matches(phoneRegex)) {
      return ResponseEntity.badRequest().body("Wrong phone number format");
    }

    Owner owner = ownerService.createOwner(ownerCreateDTO.fullName(), cleanedPhoneNumber);

    return ResponseEntity.status(HttpStatus.CREATED).body(owner);
  }

  @PutMapping("/{ownerId}")
  public ResponseEntity<?> updateOwner(@PathVariable UUID ownerId, @RequestBody OwnerCreateDTO ownerUpdateDTO) {
    if (ownerUpdateDTO.fullName() == null || ownerUpdateDTO.phoneNumber() == null ||
      ownerUpdateDTO.fullName().isBlank() || ownerUpdateDTO.phoneNumber().isBlank()) {
      return ResponseEntity.badRequest().body("Full name and phone should not be empty");
    }
    if (ownerUpdateDTO.fullName().length() < 2) {
      return ResponseEntity.badRequest().body("Full name should be at least 2 characters long");
    }
    String cleanedPhoneNumber = ownerUpdateDTO.phoneNumber().replaceAll("[^\\d+]", "");
    String phoneRegex = "^(\\+7|8)?\\d{10}$";
    if (!cleanedPhoneNumber.matches(phoneRegex)) {
      log.warn(cleanedPhoneNumber);
      return ResponseEntity.badRequest().body("Wrong phone number format");
    }

    Owner owner = ownerService.updateOwner(ownerId, ownerUpdateDTO.fullName(), cleanedPhoneNumber);

    return ResponseEntity.ok(owner);
  }

  @DeleteMapping("/{ownerId}")
  public ResponseEntity<?> deleteOwner(@PathVariable UUID ownerId) {
    if (ownerService.deleteOwner(ownerId)) {
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }
}
