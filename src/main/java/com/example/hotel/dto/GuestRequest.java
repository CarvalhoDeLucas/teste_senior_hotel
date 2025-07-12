package com.example.hotel.dto;

import jakarta.validation.constraints.NotNull;

public record GuestRequest(
        String name,
        String document,
        String phone
) {}
