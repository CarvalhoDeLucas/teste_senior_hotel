package com.example.hotel.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AtLeastOneFieldNotNullValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneFieldNotNull {
    String message() default "At least one of name, document or phone must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
