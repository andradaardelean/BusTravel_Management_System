package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RoutesDTO {
    String startDateTime;
    String endDateTime;
    String startLocation;
    String endLocation;
    Integer availableSeats;
    Double pricePerSeat;
    Integer totalSeats;
}
