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
@Table(name = "owners")
public class Owner {
  @Id
  private UUID id;

  @Column(name = "full_name", nullable = false, columnDefinition = "text")
  private String fullName;

  @Column(unique = true, nullable = false, columnDefinition = "text")
  private String phoneNumber;
}
