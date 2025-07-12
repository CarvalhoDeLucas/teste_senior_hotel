package com.example.hotel.repository;

import com.example.hotel.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {

    Optional<Guest> findByIdAndDeletedAtIsNull(UUID id);

    List<Guest> findAllByDeletedAtIsNull();

    @Query("""
        SELECT g FROM Guest g
        WHERE (
            LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))
            OR LOWER(g.document) LIKE LOWER(CONCAT('%', :document, '%'))
            OR LOWER(g.phone) LIKE LOWER(CONCAT('%', :phone, '%'))
        )
        AND g.deletedAt IS NULL
    """)
    List<Guest> searchActiveGuests(@Param("name") String name,
                                   @Param("document") String document,
                                   @Param("phone") String phone);

}