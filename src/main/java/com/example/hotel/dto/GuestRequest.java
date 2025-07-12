package com.example.hotel.dto;

import com.example.hotel.validation.AtLeastOneFieldNotNull;

@AtLeastOneFieldNotNull
public record GuestRequest(
        String name,
        String document,
        String phone
) {}
