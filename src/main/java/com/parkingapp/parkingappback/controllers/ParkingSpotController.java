package com.parkingapp.parkingappback.controllers;

import com.parkingapp.parkingappback.DTOs.request.ParkingSpotCreateDTO;
import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.services.ParkingSpotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/parkingSpots")
@AllArgsConstructor
public class ParkingSpotController {
  private final ParkingSpotService parkingSpotService;

  @GetMapping
  public ResponseEntity<?> getAllParkingSpots(){
    List<ParkingSpot> parkingSpots = parkingSpotService.getAllParkingSpots();

    return ResponseEntity.ok(parkingSpots);
  }

  @PostMapping
  public ResponseEntity<?> createParkingSpot(@RequestBody ParkingSpotCreateDTO parkingSpotCreateDTO){
    if (parkingSpotCreateDTO.spotNumber() == null || parkingSpotCreateDTO.spotNumber().isBlank()){
      return ResponseEntity.badRequest().body("Spot number should not be empty");
    }

    ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(parkingSpotCreateDTO.spotNumber());

    return ResponseEntity.ok(true);
  }

  @PutMapping("/{parkingSpotId}")
  public ResponseEntity<?> updateParkingSpot(
    @PathVariable UUID parkingSpotId,
    @RequestBody ParkingSpotCreateDTO parkingSpotCreateDTO
  ){
    if (parkingSpotCreateDTO.spotNumber() == null || parkingSpotCreateDTO.spotNumber().isBlank()){
      return ResponseEntity.badRequest().body("Spot number should not be empty");
    }

    ParkingSpot parkingSpot = parkingSpotService.updateParkingSpotNumber(parkingSpotId,
      parkingSpotCreateDTO.spotNumber());

    return ResponseEntity.ok(true );
  }

  @DeleteMapping("/{parkingSpotId}")
  public ResponseEntity<?> deleteParkingSpot(@PathVariable UUID parkingSpotId){
    if (parkingSpotService.deleteParkingSpot(parkingSpotId)){
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }
}
