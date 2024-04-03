package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDTO {
    private int passengersNo;
    private String time;
    private int routeId;
    private String type;
}
