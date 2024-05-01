package com.licenta.bustravel.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BookingLinkDTO {
    private int id;
    private BookingDTO booking;
    private StopsDTO fromStop;
    private StopsDTO toStop;
    private Integer order;
    private String distanceText;
    private String durationText;
    private Double price;
    private String startTime;
    private String endTime;
}
