package com.licenta.bustravel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.licenta.bustravel.model.enums.RecurrenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "routes")
@Builder
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
    @JsonIgnore
    @JoinColumn(name = "company_id")
    private CompanyEntity companyEntity;

    @Column(name = "recurrence_no")
    private Integer reccurencyNo;

    @Column(name = "recurrence_type")
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @OneToMany(mappedBy = "route",fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<LinkEntity> links = new ArrayList<>();

    @Override
    public String toString() {
        return "RouteEntity{" + "id=" + id + ", startDateTime=" + startDateTime + ", endDateTime=" + endDateTime + "," +
                " startLocation='" + startLocation + '\'' + ", endLocation='" + endLocation + '\'' + ", " +
                "availableSeats=" + availableSeats + ", totalSeats=" + totalSeats + ", price=" + price + ", " +
                "reccurencyNo=" + reccurencyNo + ", recurrenceType=" + recurrenceType + '}';
    }


}
