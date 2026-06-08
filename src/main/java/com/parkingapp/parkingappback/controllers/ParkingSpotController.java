package com.parkingapp.parkingappback.controllers;

import com.parkingapp.parkingappback.DTOs.request.ParkingSpotCreateDTO;
import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.services.ParkingSpotService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(parkingSpotCreateDTO.spotNumber());

    return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpot);
  }

  @PutMapping("/{parkingSpotId}")
  public ResponseEntity<?> updateParkingSpot(
    @PathVariable UUID parkingSpotId,
    @RequestBody ParkingSpotCreateDTO parkingSpotUpdateDTO
  ){
    ParkingSpot parkingSpot = parkingSpotService.updateParkingSpotNumber(parkingSpotId, parkingSpotUpdateDTO.spotNumber());

    return ResponseEntity.ok(parkingSpot);
  }

  @DeleteMapping("/{parkingSpotId}")
  public ResponseEntity<?> deleteParkingSpot(@PathVariable UUID parkingSpotId){
    if (parkingSpotService.deleteParkingSpot(parkingSpotId)){
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }
}
