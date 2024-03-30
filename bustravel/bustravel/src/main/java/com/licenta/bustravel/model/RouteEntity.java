package com.licenta.bustravel.model;

import com.licenta.bustravel.model.enums.RecurrenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "routes")
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name= "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(name = "start_location")
    private String startLocation;

    @Column(name = "end_location")
    private String endLocation;

    @Column(name = "available_seats")
    private int availableSeats;
    @Column(name = "total_seats")
    private int totalSeats;

    @Column(name = "price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity companyEntity;

    @OneToMany(mappedBy = "routeEntity",fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<BookingEntity> bookingList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "intermediate_routes",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "stop_id"))
    private List<StopEntity> stopEntities = new ArrayList<>();

    @Column(name = "recurrence_no")
    private Integer reccurencyNo;

    @Column(name = "recurrence_type")
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

}
