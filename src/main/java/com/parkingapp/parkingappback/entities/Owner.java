package com.parkingapp.parkingappback.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "owners", indexes = {
  @Index(name = "idx_owners_full_name", columnList = "full_name")
})
public class Owner {
  @Id
  private UUID id;

  @Column(name = "full_name", nullable = false, columnDefinition = "text")
  private String fullName;

  @Column(unique = true, nullable = false, columnDefinition = "text")
  private String phoneNumber;
}
