package com.parkingapp.parkingappback.DTOs;

import java.util.UUID;

public record VehicleCreateDTO(String licensePlate, String brand, String model, UUID ownerId) {
}
