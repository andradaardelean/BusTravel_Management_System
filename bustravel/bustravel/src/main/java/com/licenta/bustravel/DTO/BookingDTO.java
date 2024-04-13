package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BookingDTO {
    private int id;
    private int passengersNo;
    private int routeId;
    private String type;
}
