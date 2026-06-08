package com.parkingapp.parkingappback.controllers;

import com.parkingapp.parkingappback.DTOs.request.OwnerCreateDTO;
import com.parkingapp.parkingappback.entities.Owner;
import com.parkingapp.parkingappback.services.OwnerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<List<Owner>> getAllOwners(@RequestParam(required = false) String fullName) {
    List<Owner> owners = ownerService.getAllOwners(fullName);

    return ResponseEntity.ok(owners);
  }

  @PostMapping
  public ResponseEntity<?> createOwner(@RequestBody OwnerCreateDTO ownerCreateDTO) {
    Owner owner = ownerService.createOwner(ownerCreateDTO.fullName(), ownerCreateDTO.phoneNumber());

    return ResponseEntity.status(HttpStatus.CREATED).body(owner);
  }

  @PutMapping("/{ownerId}")
  public ResponseEntity<?> updateOwner(@PathVariable UUID ownerId, @RequestBody OwnerCreateDTO ownerUpdateDTO) {
    Owner owner = ownerService.updateOwner(ownerId, ownerUpdateDTO.fullName(), ownerUpdateDTO.phoneNumber());

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
