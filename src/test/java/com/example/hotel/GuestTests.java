package com.example.hotel;

import com.example.hotel.model.Guest;
import com.example.hotel.repository.CheckinRepository;
import com.example.hotel.repository.GuestRepository;
import com.example.hotel.service.GuestService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuestTests {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private CheckinRepository checkinRepository;

    @InjectMocks
    private GuestService guestService;

    private Guest guest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        guest = Guest.builder()
                .id(UUID.randomUUID())
                .name("Lucas Eduardo")
                .document("12345678999")
                .phone("999999999")
                .totalSpent(BigDecimal.ZERO)
                .build();
    }

    @Test
    void testCreate() {
        when(guestRepository.save(guest)).thenReturn(guest);
        Guest saved = guestService.create(guest);
        assertThat(saved).isEqualTo(guest);
        verify(guestRepository).save(guest);
    }

    @Test
    void testUpdate() {
        UUID id = guest.getId();
        Guest updated = new Guest();
        updated.setName("Maria");
        updated.setDocument("11111111111");
        updated.setPhone("888888888");

        when(guestRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(inv -> inv.getArgument(0));

        Guest result = guestService.update(id, updated);

        assertThat(result.getName()).isEqualTo("Maria");
        assertThat(result.getDocument()).isEqualTo("11111111111");
        assertThat(result.getPhone()).isEqualTo("888888888");
        verify(guestRepository).save(guest);
    }

    @Test
    void testDeleteSuccess() {
        UUID id = guest.getId();
        when(guestRepository.findById(id)).thenReturn(Optional.of(guest));
        when(checkinRepository.existsActiveCheckinForGuest(id)).thenReturn(false);

        guestService.delete(id);

        assertThat(guest.getDeletedAt()).isNotNull();
        verify(guestRepository).save(guest);
    }

    @Test
    void testDeleteWithActiveCheckinThrowsException() {
        UUID id = guest.getId();
        when(guestRepository.findById(id)).thenReturn(Optional.of(guest));
        when(checkinRepository.existsActiveCheckinForGuest(id)).thenReturn(true);

        assertThatThrownBy(() -> guestService.delete(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Guest has an active check-in and cannot be deleted.");
    }

    @Test
    void testFindByIdSuccess() {
        UUID id = guest.getId();
        when(guestRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(guest));
        Guest found = guestService.findById(id);
        assertThat(found).isEqualTo(guest);
    }

    @Test
    void testFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(guestRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> guestService.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testListAll() {
        List<Guest> guests = List.of(guest);
        when(guestRepository.findAllByDeletedAtIsNull()).thenReturn(guests);
        List<Guest> result = guestService.listAll();
        assertThat(result).containsExactly(guest);
    }

    @Test
    void testSearch() {
        List<Guest> guests = List.of(guest);
        when(guestRepository.searchActiveGuests("query", "query", "query")).thenReturn(guests);
        List<Guest> result = guestService.search("query");
        assertThat(result).containsExactly(guest);
    }
}

