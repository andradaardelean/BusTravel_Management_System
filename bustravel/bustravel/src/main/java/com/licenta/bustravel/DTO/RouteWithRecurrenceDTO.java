package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RouteWithRecurrenceDTO {
    Integer id;
    String startDateTime;
    String endDateTime;
    String startLocation;
    String endLocation;
    Integer availableSeats;
    Double pricePerSeat;
    Integer totalSeats;
    String recurrenceType;
}
