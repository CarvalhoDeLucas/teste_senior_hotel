package com.example.hotel.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "checkin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkin {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "checkin_time", nullable = false)
    private LocalDateTime checkinTime;

    @Column(name = "checkout_time")
    private LocalDateTime checkoutTime;

    @Column(name = "has_vehicle", nullable = false)
    private boolean hasVehicle;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "lodging_amount")
    private BigDecimal lodgingAmount;

    @Column(name = "parking_amount")
    private BigDecimal parkingAmount;

}