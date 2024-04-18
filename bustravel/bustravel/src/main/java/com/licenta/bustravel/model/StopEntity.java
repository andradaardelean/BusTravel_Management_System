package com.licenta.bustravel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "stops")
public class StopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private int id;

    @Column(name = "location")
    private String location;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "fromStop", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}    )
    private List<LinkEntity> fromLinks = new ArrayList<>();

    @OneToMany(mappedBy = "toStop", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<LinkEntity> toLinks = new ArrayList<>();


}
