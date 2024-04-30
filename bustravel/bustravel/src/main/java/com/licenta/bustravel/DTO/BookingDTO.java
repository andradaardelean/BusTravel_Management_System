package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingDTO {
    private int id;
    private int passengersNo;
    private String type;
}
