package com.parkingapp.parkingappback.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehicles", indexes = {
  @Index(name = "idx_vehicles_owner_id", columnList = "owner_id")
})
public class Vehicle {
  @Id
  private UUID id;

  @Column(name = "license_plate", unique = true, nullable = false, columnDefinition = "text")
  private String licensePlate;

  @Column(columnDefinition = "text")
  private String brand;

  @Column(columnDefinition = "text")
  private String model;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Owner owner;
}
