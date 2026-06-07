package com.parkingapp.parkingappback.exceptions.owners;

import java.util.UUID;

public class OwnerNotFoundException extends RuntimeException{
  public OwnerNotFoundException(UUID ownerId){
    super("Owner not found with id: " + ownerId);
  }
}
