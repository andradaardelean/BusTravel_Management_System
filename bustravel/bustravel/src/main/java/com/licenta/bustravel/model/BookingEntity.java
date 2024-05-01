package com.licenta.bustravel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.licenta.bustravel.model.enums.BookingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
@Builder
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "passagers_no")
    private int passegersNo;
    @Column(name = "time")
    private LocalDateTime time;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private BookingType type;

    @OneToMany(mappedBy = "booking", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<BookingLinkEntity> bookingLinks;

    @Override
    public String toString() {
        return "BookingEntity{" + "id=" + id + ", passegersNo=" + passegersNo + ", time=" + time + ", type=" + type + '}';
    }
}
