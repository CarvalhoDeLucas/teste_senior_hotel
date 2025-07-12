package com.example.hotel.service;

import com.example.hotel.model.Guest;
import com.example.hotel.repository.CheckinRepository;
import com.example.hotel.repository.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final CheckinRepository checkinRepository;

    public Guest create(Guest guest) {
        return guestRepository.save(guest);
    }

    public Guest update(UUID id, Guest updatedGuest) {
        Guest existing = findById(id);
        existing.setName(updatedGuest.getName());
        existing.setDocument(updatedGuest.getDocument());
        existing.setPhone(updatedGuest.getPhone());

        return guestRepository.save(existing);
    }

    public void delete(UUID id) {
        Guest guest = guestRepository.findById(id)
                .filter(g -> g.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found or already deleted"));

        boolean hasActiveCheckin = checkinRepository.existsActiveCheckinForGuest(guest.getId());
        if (hasActiveCheckin) throw new IllegalStateException("Guest has an active check-in and cannot be deleted.");

        boolean hasFutureCheckin = checkinRepository.existsFutureCheckinForGuest(guest.getId());
        if (hasFutureCheckin) throw new IllegalStateException("Guest has a scheduled check-in and cannot be deleted.");

        guest.setDeletedAt(LocalDateTime.now());
        guestRepository.save(guest);
    }


    public Guest findById(UUID id) {
        return guestRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found or already deleted"));
    }

    public List<Guest> listAll() {
        return guestRepository.findAllByDeletedAtIsNull();
    }

    public List<Guest> search(String query) {
        return guestRepository.searchActiveGuests(query, query, query);
    }
}