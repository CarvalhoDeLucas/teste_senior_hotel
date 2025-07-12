package com.example.hotel.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    private int status;
    private String message;
    private Map<String, String> errors;

}
