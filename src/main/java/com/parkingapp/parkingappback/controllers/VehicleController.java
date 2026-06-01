package com.parkingapp.parkingappback.controllers;

import com.parkingapp.parkingappback.DTOs.request.VehicleCreateDTO;
import com.parkingapp.parkingappback.entities.Vehicle;
import com.parkingapp.parkingappback.services.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/vehicles")
@AllArgsConstructor
public class VehicleController {
  private final VehicleService vehicleService;

  @GetMapping
  public ResponseEntity<?> getAllVehicles(@RequestParam(required = false) String licensePlate){
    List<Vehicle> vehicles = vehicleService.getAllVehicles(licensePlate);

    return ResponseEntity.ok(vehicles);
  }

  @PostMapping
  public ResponseEntity<?> createVehicle(@RequestBody VehicleCreateDTO vehicleCreateDTO){
    if (vehicleCreateDTO.licensePlate() == null || vehicleCreateDTO.licensePlate().isBlank()){
      return ResponseEntity.badRequest().body("License plate should not be empty");
    }

    Vehicle vehicle = vehicleService.createVehicle(vehicleCreateDTO.licensePlate(), vehicleCreateDTO.brand(),
      vehicleCreateDTO.model(), vehicleCreateDTO.ownerId());

    return ResponseEntity.ok(true);
  }

  @PutMapping("/{vehicleId}")
  public ResponseEntity<?> updateVehicle(@PathVariable UUID vehicleId, @RequestBody VehicleCreateDTO vehicleUpdateDTO){
    if (vehicleUpdateDTO.licensePlate() == null || vehicleUpdateDTO.licensePlate().isBlank()){
      return ResponseEntity.badRequest().body("License plate should not be empty");
    }

    Vehicle vehicle = vehicleService.updateVehicle(vehicleId, vehicleUpdateDTO.licensePlate(),
      vehicleUpdateDTO.brand(), vehicleUpdateDTO.model(), vehicleUpdateDTO.ownerId());

    return ResponseEntity.ok(true);
  }

  @DeleteMapping("/{vehicleId}")
  public ResponseEntity<?> deleteVehicle(@PathVariable UUID vehicleId){
    if (vehicleService.deleteVehicle(vehicleId)){
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }
}
