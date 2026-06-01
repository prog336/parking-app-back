package com.parkingapp.parkingappback.controllers;

import com.parkingapp.parkingappback.DTOs.request.OwnerCreateDTO;
import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.services.OwnerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/owners")
@AllArgsConstructor
public class OwnerController {
  private final OwnerService ownerService;

  @GetMapping
  public ResponseEntity<?> getAllOwners(@RequestParam(required = false) String fullName){
    List<Owner> owners = ownerService.getAllOwners(fullName);

    return ResponseEntity.ok(owners);
  }

  @PostMapping
  public ResponseEntity<?> createOwner(@RequestBody OwnerCreateDTO ownerCreateDTO){
    if (ownerCreateDTO.fullName() == null || ownerCreateDTO.phoneNumber() == null ||
      ownerCreateDTO.fullName().isBlank() || ownerCreateDTO.phoneNumber().isBlank()){
      return ResponseEntity.badRequest().body("Full name and phone should not be empty");
    }
    if (ownerCreateDTO.fullName().length() < 2){
      return ResponseEntity.badRequest().body("Full name should be at least 2 characters long");
    }
    String phoneRegex = "^(\\+7|8)?[\\s\\-]?\\(?\\d{3}\\)?[\\s\\-]?\\d{3}[\\s\\-]?\\d{2}[\\s\\-]?\\d{2}$";
    if (!ownerCreateDTO.phoneNumber().matches(phoneRegex)){
      return ResponseEntity.badRequest().body("Wrong phone number format");
    }

    Owner owner = ownerService.createOwner(ownerCreateDTO.fullName(), ownerCreateDTO.phoneNumber());

    return ResponseEntity.ok(true);
  }

  @PutMapping("/{ownerId}")
  public ResponseEntity<?> updateOwner(@PathVariable UUID ownerId, @RequestBody OwnerCreateDTO ownerUpdateDTO){
    if (ownerUpdateDTO.fullName() == null || ownerUpdateDTO.phoneNumber() == null ||
      ownerUpdateDTO.fullName().isBlank() || ownerUpdateDTO.phoneNumber().isBlank()){
      return ResponseEntity.badRequest().body("Full name and phone should not be empty");
    }
    if (ownerUpdateDTO.fullName().length() < 2){
      return ResponseEntity.badRequest().body("Full name should be at least 2 characters long");
    }
    String phoneRegex = "^(\\+7|8)?[\\s\\-]?\\(?\\d{3}\\)?[\\s\\-]?\\d{3}[\\s\\-]?\\d{2}[\\s\\-]?\\d{2}$";
    if (!ownerUpdateDTO.phoneNumber().matches(phoneRegex)){
      return ResponseEntity.badRequest().body("Wrong phone number format");
    }

    Owner owner = ownerService.updateOwner(ownerId, ownerUpdateDTO.fullName(), ownerUpdateDTO.phoneNumber());

    return ResponseEntity.ok(true);
  }

  @DeleteMapping("/{ownerId}")
  public ResponseEntity<?> deleteOwner(@PathVariable UUID ownerId){
    if (ownerService.deleteOwner(ownerId)){
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }
}
