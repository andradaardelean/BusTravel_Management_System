package com.licenta.bustravel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "passagers_no")
    private int passagersNo;
    @Column(name = "time")
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity routeEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

}
