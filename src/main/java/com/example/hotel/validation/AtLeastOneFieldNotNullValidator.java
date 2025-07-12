package com.example.hotel.validation;

import com.example.hotel.dto.GuestRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneFieldNotNullValidator implements ConstraintValidator<AtLeastOneFieldNotNull, GuestRequest> {

    @Override
    public boolean isValid(GuestRequest guest, ConstraintValidatorContext context) {
        return guest != null && (
                guest.name() != null && !guest.name().isBlank()
                        || guest.document() != null && !guest.document().isBlank()
                        || guest.phone() != null && !guest.phone().isBlank()
        );
    }
}
