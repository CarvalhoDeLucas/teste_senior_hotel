package com.example.hotel.controller;

import com.example.hotel.dto.CheckinRequest;
import com.example.hotel.dto.CheckinResponse;
import com.example.hotel.dto.GuestStayInfo;
import com.example.hotel.model.Checkin;
import com.example.hotel.service.CheckinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @PostMapping
    public ResponseEntity<CheckinResponse> checkIn(@Valid @RequestBody CheckinRequest request) {
        Checkin saved = checkinService.checkIn(
                request.guest(),
                Checkin.builder()
                        .checkinTime(request.checkinTime())
                        .checkoutTime(request.checkoutTime())
                        .hasVehicle(request.hasVehicle())
                        .build()
        );

        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/active")
    public ResponseEntity<List<GuestStayInfo>> activeGuests() {
        return ResponseEntity.ok(checkinService.findActiveGuests());
    }

    @GetMapping("/past")
    public ResponseEntity<List<GuestStayInfo>> pastGuests() {
        return ResponseEntity.ok(checkinService.findPastGuests());
    }

    private CheckinResponse toResponse(Checkin c) {
        return new CheckinResponse(
                c.getId(),
                c.getGuest().getId(),
                c.getCheckinTime(),
                c.getCheckoutTime(),
                c.isHasVehicle(),
                c.getLodgingAmount(),
                c.getParkingAmount(),
                c.getTotalAmount()
        );
    }

}