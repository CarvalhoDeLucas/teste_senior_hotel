package com.example.hotel.controller;

import com.example.hotel.dto.GuestRequest;
import com.example.hotel.dto.GuestResponse;
import com.example.hotel.model.Guest;
import com.example.hotel.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @PostMapping
    public ResponseEntity<GuestResponse> create(@Valid @RequestBody GuestRequest request) {
        Guest guest = guestService.create(toEntity(request));

        return ResponseEntity.ok(toResponse(guest));
    }

    @GetMapping
    public ResponseEntity<List<GuestResponse>> getAll(@RequestParam(required = false) String search) {
        List<Guest> guests = (search == null || search.isBlank())
                ? guestService.listAll()
                : guestService.search(search);

        return ResponseEntity.ok(guests.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponse> getById(@PathVariable UUID id) {
        Guest guest = guestService.findById(id);

        return ResponseEntity.ok(toResponse(guest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestResponse> update(@PathVariable UUID id, @Valid @RequestBody GuestRequest request) {
        Guest updated = guestService.update(id, toEntity(request));

        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        guestService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private Guest toEntity(GuestRequest dto) {
        return Guest.builder()
                .name(dto.name())
                .document(dto.document())
                .phone(dto.phone())
                .totalSpent(BigDecimal.ZERO)
                .build();
    }

    private GuestResponse toResponse(Guest guest) {
        return new GuestResponse(
                guest.getId(),
                guest.getName(),
                guest.getDocument(),
                guest.getPhone(),
                guest.getTotalSpent(),
                guest.getCreatedAt(),
                guest.getUpdatedAt()
        );
    }
}