package com.example.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CheckinResponse(
        UUID id,
        UUID guestId,
        LocalDateTime checkinTime,
        LocalDateTime checkoutTime,
        boolean hasVehicle,
        BigDecimal lodgingAmount,
        BigDecimal parkingAmount,
        BigDecimal totalAmount
) {}