package com.parkingapp.parkingappback.exceptions;

import com.parkingapp.parkingappback.exceptions.bookings.BookingNotFoundException;
import com.parkingapp.parkingappback.exceptions.bookings.DuplicateVehicleOrParkingSpotException;
import com.parkingapp.parkingappback.exceptions.owners.DuplicatePhoneNumberException;
import com.parkingapp.parkingappback.exceptions.owners.OwnerNotFoundException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.DuplicateSpotNumberException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.ParkingSpotAlreadyOccupiedException;
import com.parkingapp.parkingappback.exceptions.parkingSpots.ParkingSpotNotFoundException;
import com.parkingapp.parkingappback.exceptions.vehicles.DuplicateLicensePlateException;
import com.parkingapp.parkingappback.exceptions.vehicles.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(OwnerNotFoundException.class)
  public ResponseEntity<?> handleOwnerNotFound(OwnerNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(VehicleNotFoundException.class)
  public ResponseEntity<?> handleVehicleNotFound(VehicleNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(ParkingSpotNotFoundException.class)
  public ResponseEntity<?> handleParkingSpotNotFound(ParkingSpotNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(BookingNotFoundException.class)
  public ResponseEntity<?> handleBookingNotFound(BookingNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(DuplicatePhoneNumberException.class)
  public ResponseEntity<?> handleDuplicatePhoneNumber(DuplicatePhoneNumberException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(DuplicateLicensePlateException.class)
  public ResponseEntity<?> handleDuplicateLicensePlate(DuplicateLicensePlateException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(DuplicateSpotNumberException.class)
  public ResponseEntity<?> handleDuplicateSpotNumber(DuplicateSpotNumberException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(DuplicateVehicleOrParkingSpotException.class)
  public ResponseEntity<?> handleDuplicateVehicleOrParkingSpot(DuplicateVehicleOrParkingSpotException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(ParkingSpotAlreadyOccupiedException.class)
  public ResponseEntity<?> handleParkingSpotAlreadyOccupied(ParkingSpotAlreadyOccupiedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleUnknownException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error: " + ex.getMessage());
  }
}
