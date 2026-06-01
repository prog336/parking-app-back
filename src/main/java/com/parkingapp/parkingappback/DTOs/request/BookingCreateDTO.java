package com.parkingapp.parkingappback.DTOs;

import java.time.Instant;
import java.util.UUID;

public record BookingCreateDTO(UUID parkingSpotId, UUID vehicleId, Instant startTime, Instant endTime) {
}
