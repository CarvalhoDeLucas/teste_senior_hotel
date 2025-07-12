package com.example.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GuestStayInfo(
        UUID guestId,
        String name,
        String document,
        BigDecimal totalSpent,
        BigDecimal lastStayAmount
) {}