package com.parkingapp.parkingappback.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
  @Id
  private UUID id;

  @Column(name = "start_time", nullable = false)
  private Instant startTime;

  @Column(name = "end_time", nullable = false)
  private Instant endTime;

  @Column(name = "is_paid", nullable = false)
  private boolean isPaid;

  @Column(nullable = false)
  private int cost;

  @OneToOne
  @JoinColumn(name = "parking_spot_id", unique = true, nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ParkingSpot parkingSpot;

  @OneToOne
  @JoinColumn(name = "vehicle_id", unique = true, nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Vehicle vehicle;
}
