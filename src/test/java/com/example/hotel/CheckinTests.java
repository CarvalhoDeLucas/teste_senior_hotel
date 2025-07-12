package com.example.hotel;

import com.example.hotel.dto.GuestRequest;
import com.example.hotel.dto.GuestStayInfo;
import com.example.hotel.model.Checkin;
import com.example.hotel.model.Guest;
import com.example.hotel.repository.CheckinRepository;
import com.example.hotel.repository.GuestRepository;
import com.example.hotel.service.CheckinService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckinTests {

    @Mock
    private CheckinRepository checkinRepository;

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private CheckinService checkinService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckInSuccess() {
        // Arrange
        Guest guest = Guest.builder()
                .id(UUID.randomUUID())
                .name("Lucas Eduardo")
                .document("12345678900")
                .phone("11999999999")
                .totalSpent(BigDecimal.ZERO)
                .build();

        Checkin checkin = new Checkin();
        checkin.setCheckinTime(LocalDateTime.now().plusDays(1));
        checkin.setCheckoutTime(LocalDateTime.now().plusDays(2));
        checkin.setHasVehicle(true);

        GuestRequest guestRequest = new GuestRequest("Lucas Eduardo", "12345678900", "11999999999");

        when(guestRepository.searchActiveGuests(anyString(), anyString(), anyString()))
                .thenReturn(List.of(guest));
        when(checkinRepository.hasActiveCheckin(guest)).thenReturn(false);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);
        when(checkinRepository.save(any(Checkin.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Checkin result = checkinService.checkIn(guestRequest, checkin);

        // Assert
        assertNotNull(result);
        assertEquals(guest, result.getGuest());
        assertTrue(result.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCheckInGuestNotFound() {
        GuestRequest guestRequest = new GuestRequest("AAAA", "000", "000");
        Checkin checkin = new Checkin();
        checkin.setCheckinTime(LocalDateTime.now().plusDays(1));
        checkin.setCheckoutTime(LocalDateTime.now().plusDays(2));

        when(guestRepository.searchActiveGuests(any(), any(), any()))
                .thenReturn(List.of());

        assertThrows(EntityNotFoundException.class,
                () -> checkinService.checkIn(guestRequest, checkin));
    }

    @Test
    void testCheckInAlreadyHasActiveCheckin() {
        Guest guest = Guest.builder().build();
        GuestRequest guestRequest = new GuestRequest("A", "B", "C");

        Checkin checkin = new Checkin();
        checkin.setCheckinTime(LocalDateTime.now().plusDays(1));
        checkin.setCheckoutTime(LocalDateTime.now().plusDays(2));

        when(guestRepository.searchActiveGuests(any(), any(), any()))
                .thenReturn(List.of(guest));
        when(checkinRepository.hasActiveCheckin(guest)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> checkinService.checkIn(guestRequest, checkin));
    }

    @Test
    void testFindActiveGuests() {
        Guest guest = Guest.builder()
                .id(UUID.randomUUID())
                .name("Osnildo")
                .document("123456")
                .totalSpent(BigDecimal.valueOf(500))
                .build();

        Checkin checkin = new Checkin();
        checkin.setGuest(guest);
        checkin.setTotalAmount(BigDecimal.valueOf(200));

        when(checkinRepository.findActiveGuestsWithGuest()).thenReturn(List.of(checkin));

        List<GuestStayInfo> result = checkinService.findActiveGuests();

        assertEquals(1, result.size());
        assertEquals("Osnildo", result.getFirst().name());
        assertEquals(BigDecimal.valueOf(500), result.getFirst().totalSpent());
    }

    @Test
    void testFindPastGuests() {
        Guest guest = Guest.builder()
                .id(UUID.randomUUID())
                .name("Sarah")
                .document("987654321")
                .totalSpent(BigDecimal.valueOf(1000))
                .build();

        Checkin checkin1 = new Checkin();
        checkin1.setGuest(guest);
        checkin1.setCheckoutTime(LocalDateTime.now().minusDays(2));
        checkin1.setTotalAmount(BigDecimal.valueOf(300));

        Checkin checkin2 = new Checkin();
        checkin2.setGuest(guest);
        checkin2.setCheckoutTime(LocalDateTime.now().minusDays(1));
        checkin2.setTotalAmount(BigDecimal.valueOf(500));

        when(checkinRepository.findByCheckoutTimeBefore(any())).thenReturn(List.of(checkin1, checkin2));

        List<GuestStayInfo> result = checkinService.findPastGuests();

        assertEquals(1, result.size());
        assertEquals("Sarah", result.getFirst().name());
        assertEquals(BigDecimal.valueOf(500), result.getFirst().lastStayAmount());
    }

}

