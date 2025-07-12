package com.example.hotel.repository;

import com.example.hotel.model.Checkin;
import com.example.hotel.model.Guest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CheckinRepository extends JpaRepository<Checkin, UUID> {

    @Query("""
        SELECT COUNT(c) > 0 FROM Checkin c
        WHERE c.guest = :guest
          AND c.checkinTime <= CURRENT_TIMESTAMP
          AND (c.checkoutTime IS NULL OR c.checkoutTime > CURRENT_TIMESTAMP)
    """)
    boolean hasActiveCheckin(@Param("guest") Guest guest);


    @EntityGraph(attributePaths = "guest")
    @Query("SELECT c FROM Checkin c WHERE c.checkinTime <= CURRENT_TIMESTAMP AND (c.checkoutTime IS NULL OR c.checkoutTime > CURRENT_TIMESTAMP)")
    List<Checkin> findActiveGuestsWithGuest();

    @EntityGraph(attributePaths = "guest")
    List<Checkin> findByCheckoutTimeBefore(LocalDateTime now);

    @Query("""
        SELECT COUNT(c) > 0
        FROM Checkin c
        WHERE c.guest.id = :guestId
          AND c.checkinTime <= CURRENT_TIMESTAMP
          AND (c.checkoutTime IS NULL OR c.checkoutTime > CURRENT_TIMESTAMP)
    """)
    boolean existsActiveCheckinForGuest(@Param("guestId") UUID guestId);

    @Query("SELECT COUNT(c) > 0 FROM Checkin c WHERE c.guest.id = :guestId AND c.checkinTime > CURRENT_TIMESTAMP")
    boolean existsFutureCheckinForGuest(@Param("guestId") UUID guestId);

}