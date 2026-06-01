package com.parkingapp.parkingappback.DTOs.response;

import com.parkingapp.parkingappback.entities.ParkingSpot;
import com.parkingapp.parkingappback.entities.Vehicle;

import java.util.UUID;

public record BookingReturnDTO(
  UUID id,
  String startTime,
  String endTime,
  boolean isPaid,
  int cost,
  ParkingSpot parkingSpot,
  Vehicle vehicle
) {
}
