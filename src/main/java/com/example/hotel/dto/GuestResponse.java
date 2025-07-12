package com.example.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GuestResponse(
        UUID id,
        String name,
        String document,
        String phone,
        BigDecimal totalSpent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
