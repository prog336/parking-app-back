package com.parkingapp.parkingappback.DTOs.request;

import java.time.Instant;
import java.util.UUID;

public record BookingCreateDTO(UUID parkingSpotId, UUID vehicleId, Instant startTime, Instant endTime) {
}
