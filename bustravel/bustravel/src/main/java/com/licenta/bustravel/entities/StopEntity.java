package com.licenta.bustravel.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "stops")
public class StopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private int id;

    @Column(name = "location")
    private String location;
    @Column(name = "\"order\"")
    private int order;

    @Column(name = "stop")
    private String stop;
    @Column(name = "stop_order")
    private int stopOrder;

    @ManyToMany(mappedBy = "stopEntities")
    private List<RouteEntity> routeEntityList = new ArrayList<>();

    public StopEntity(String location, int order, String stop, int stopOrder) {
        this.location = location;
        this.order = order;
        this.stop = stop;
        this.stopOrder = stopOrder;
    }
}
