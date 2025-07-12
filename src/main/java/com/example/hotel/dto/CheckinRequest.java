package com.example.hotel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheckinRequest(
        @NotNull @Valid GuestRequest guest,
        @NotNull LocalDateTime checkinTime,
        @NotNull LocalDateTime checkoutTime,
        @NotNull Boolean hasVehicle
) {}