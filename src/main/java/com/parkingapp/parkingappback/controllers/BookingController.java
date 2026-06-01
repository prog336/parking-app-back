package com.parkingapp.parkingappback.controllers;

import com.parkingapp.parkingappback.DTOs.request.BookingCreateDTO;
import com.parkingapp.parkingappback.DTOs.response.BookingReturnDTO;
import com.parkingapp.parkingappback.entities.Booking;
import com.parkingapp.parkingappback.services.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/bookings")
@AllArgsConstructor
public class BookingController {
  private final BookingService bookingService;

  @GetMapping
  public ResponseEntity<?> getAllBookings(
    @RequestParam(required = false) String licensePlate,
    @RequestParam(required = false) String fullName
  ){
    List<Booking> bookings = bookingService.getAllBookings(licensePlate, fullName);
    List<BookingReturnDTO> bookingReturnDTOs = bookings.stream().map(booking -> new BookingReturnDTO(
      booking.getId(),
      booking.getStartTime().toString(),
      booking.getEndTime().toString(),
      booking.isPaid(),
      booking.getCost(),
      booking.getParkingSpot(),
      booking.getVehicle()
    )).toList();

    return ResponseEntity.ok(bookingReturnDTOs);
  }

  @PostMapping
  public ResponseEntity<?> createBooking(@RequestBody BookingCreateDTO bookingCreateDTO){
    if (bookingCreateDTO.startTime() == null || bookingCreateDTO.endTime() == null
    || bookingCreateDTO.parkingSpotId() == null || bookingCreateDTO.vehicleId() == null){
      return ResponseEntity.badRequest().body("Start time, end time, spot id and vehicle id should not be empty");
    }
    if (bookingCreateDTO.endTime().isBefore(Instant.now())){
      return ResponseEntity.badRequest().body("End time cannot be earlier than now");
    }
    if (bookingCreateDTO.endTime().isBefore(bookingCreateDTO.startTime())){
      return ResponseEntity.badRequest().body("End time cannot be earlier than start time");
    }

    Booking booking = bookingService.createBooking(bookingCreateDTO.parkingSpotId(), bookingCreateDTO.vehicleId(),
      bookingCreateDTO.startTime(), bookingCreateDTO.endTime());

    return ResponseEntity.ok(true);
  }

  @PutMapping("/{bookingId}")
  public ResponseEntity<?> payBooking(@PathVariable UUID bookingId){
    Booking booking = bookingService.payBooking(bookingId);

    return ResponseEntity.ok(booking.isPaid());
  }

  @DeleteMapping("/{bookingId}")
  public ResponseEntity<?> deleteBooking(@PathVariable UUID bookingId){
    if (bookingService.deleteBooking(bookingId)){
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }

  @DeleteMapping("/expired")
  public ResponseEntity<?> deleteExpiredBookings(){
    if (bookingService.deleteExpiredBookings()){
      return ResponseEntity.ok(true);
    }
    return ResponseEntity.badRequest().body(false);
  }
}
