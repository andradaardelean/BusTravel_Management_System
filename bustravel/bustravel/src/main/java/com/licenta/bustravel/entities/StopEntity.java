package com.licenta.bustravel.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stops")
public class StopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private int id;

    @Column(name = "location")
    private String location;
    @Column(name = "order")
    private int order;
    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity routeEntity;

    @Column(name = "stop")
    private String stop;
    @Column(name = "stop_order")
    private int stopOrder;

}
