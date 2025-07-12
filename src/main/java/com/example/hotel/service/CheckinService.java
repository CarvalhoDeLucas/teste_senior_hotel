package com.example.hotel.service;

import com.example.hotel.dto.GuestRequest;
import com.example.hotel.dto.GuestStayInfo;
import com.example.hotel.model.Checkin;
import com.example.hotel.model.Guest;
import com.example.hotel.repository.CheckinRepository;
import com.example.hotel.repository.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final GuestRepository guestRepository;

    public Checkin checkIn(GuestRequest guestRequest, Checkin checkin) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkinTime = checkin.getCheckinTime();
        LocalDateTime checkoutTime = checkin.getCheckoutTime();

        if (checkinTime.isBefore(now)) throw new IllegalArgumentException("Check-in time cannot be in the past.");
        if (checkoutTime != null && checkoutTime.isBefore(checkinTime)) throw new IllegalArgumentException("Checkout time cannot be before check-in time.");

        // Buscar hóspede
        Guest guest = guestRepository.searchActiveGuests(
                        guestRequest.name(), guestRequest.document(), guestRequest.phone()
                ).stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            if (list.isEmpty()) throw new EntityNotFoundException("Guest not found with the given information.");
                            if (list.size() > 1) throw new IllegalStateException("Multiple guests found with the given information.");
                            return list.get(0);
                        }
                ));

        boolean hasActiveCheckin = checkinRepository.hasActiveCheckin(guest);
        if (hasActiveCheckin) throw new IllegalStateException("Guest already has an active check-in.");

        // Atribuições e cálculos
        checkin.setGuest(guest);
        calculateAmounts(checkin);

        BigDecimal newTotal = guest.getTotalSpent().add(checkin.getTotalAmount());
        guest.setTotalSpent(newTotal);
        guestRepository.save(guest);

        return checkinRepository.save(checkin);
    }

    public List<GuestStayInfo> findActiveGuests() {
        List<Checkin> activeCheckins = checkinRepository.findActiveGuestsWithGuest();

        return activeCheckins.stream()
                .map(c -> new GuestStayInfo(
                        c.getGuest().getId(),
                        c.getGuest().getName(),
                        c.getGuest().getDocument(),
                        c.getGuest().getTotalSpent(),
                        c.getTotalAmount()
                ))
                .toList();
    }


    public List<GuestStayInfo> findPastGuests() {
        List<Checkin> pastCheckins = checkinRepository.findByCheckoutTimeBefore(LocalDateTime.now());

        // Agrupar por hóspede
        return pastCheckins.stream()
                .collect(Collectors.groupingBy(Checkin::getGuest))
                .entrySet()
                .stream()
                .map(entry -> {
                    Guest guest = entry.getKey();
                    List<Checkin> guestCheckins = entry.getValue();

                    Checkin lastCheckin = guestCheckins.stream()
                            .max(Comparator.comparing(Checkin::getCheckoutTime))
                            .orElse(null);

                    return new GuestStayInfo(
                            guest.getId(),
                            guest.getName(),
                            guest.getDocument(),
                            guest.getTotalSpent(),
                            lastCheckin != null ? lastCheckin.getTotalAmount() : BigDecimal.ZERO
                    );
                })
                .toList();
    }


    private void calculateAmounts(Checkin checkin) {
        LocalDateTime in = checkin.getCheckinTime();
        LocalDateTime out = checkin.getCheckoutTime();

        if (out == null || out.isBefore(in)) throw new IllegalArgumentException("Invalid checkout date");

        long days = ChronoUnit.DAYS.between(in.toLocalDate(), out.toLocalDate()) + 1;

        BigDecimal lodging = BigDecimal.ZERO;
        BigDecimal parking = BigDecimal.ZERO;

        int DAILY_RATE_WEEKDAY = 120;
        int DAILY_RATE_WEEKEND = 150;

        int PARKING_RATE_WEEKDAY = 15;
        int PARKING_RATE_WEEKEND = 20;

        for (int i = 0; i < days; i++) {
            LocalDate current = in.toLocalDate().plusDays(i);
            DayOfWeek dow = current.getDayOfWeek();

            boolean isWeekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);

            BigDecimal dailyRate = isWeekend ? BigDecimal.valueOf(DAILY_RATE_WEEKEND) : BigDecimal.valueOf(DAILY_RATE_WEEKDAY);
            lodging = lodging.add(dailyRate);

            if (checkin.isHasVehicle()) {
                BigDecimal parkingRate = isWeekend ? BigDecimal.valueOf(PARKING_RATE_WEEKEND) : BigDecimal.valueOf(PARKING_RATE_WEEKDAY);
                parking = parking.add(parkingRate);
            }
        }

        // Verifica se irá add um dia (depois das 16:30)
        LocalTime limit = LocalTime.of(16, 30);
        if (out.toLocalTime().isAfter(limit)) {
            DayOfWeek dow = out.getDayOfWeek();
            BigDecimal extra = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY
                    ? BigDecimal.valueOf(DAILY_RATE_WEEKEND)
                    : BigDecimal.valueOf(DAILY_RATE_WEEKDAY);

            lodging = lodging.add(extra);

            if (checkin.isHasVehicle()) {
                BigDecimal parkingExtra = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY
                        ? BigDecimal.valueOf(PARKING_RATE_WEEKEND)
                        : BigDecimal.valueOf(PARKING_RATE_WEEKDAY);
                parking = parking.add(parkingExtra);
            }
        }

        checkin.setLodgingAmount(lodging);
        checkin.setParkingAmount(parking);
        checkin.setTotalAmount(lodging.add(parking));
    }
}