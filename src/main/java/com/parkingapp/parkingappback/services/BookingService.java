package com.parkingapp.parkingappback.services;

import com.parkingapp.parkingappback.entities.Booking;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookingService {
  List<Booking> getAllBookings(String licensePlate, String fullName);

  Booking createBooking(UUID parkingSpotId, UUID vehicleId, Instant startTime, Instant endTime);

  Booking payBooking(UUID bookingId);

  boolean deleteBooking(UUID bookingId);

  boolean deleteExpiredBookings();
}
