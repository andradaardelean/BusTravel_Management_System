package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteDTO {
    Integer id;
    String startDateTime;
    String endDateTime;
    String startLocation;
    String endLocation;
    Integer availableSeats;
    Double pricePerSeat;
    Integer totalSeats;
}
