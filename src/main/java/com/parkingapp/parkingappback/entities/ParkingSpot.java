package com.parkingapp.parkingappback.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "parking_spots")
public class ParkingSpot {
  @Id
  private UUID id;

  @Column(name = "spot_number", unique = true, nullable = false, columnDefinition = "text")
  private String spotNumber;

  @Column(name = "is_occupied", nullable = false)
  private boolean isOccupied;
}
