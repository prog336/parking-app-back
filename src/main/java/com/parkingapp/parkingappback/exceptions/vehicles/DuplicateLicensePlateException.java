package com.parkingapp.parkingappback.exceptions.vehicles;

public class DuplicateLicensePlateException extends RuntimeException {
  public DuplicateLicensePlateException(String licensePlate) {

    super("Vehicle with this license plate already exists: " + licensePlate);
  }
}
