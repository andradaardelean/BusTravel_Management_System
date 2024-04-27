package com.licenta.bustravel.DTO;

import lombok.Builder;

@Builder
public class BookingLinkDTO {
    private int id;
    private BookingDTO booking;
    private StopsDTO fromStop;
    private StopsDTO toStop;
    private String distanceText;
    private String durationText;
    private Double price;
    private int order;
    private String startTime;
    private String endTime;
}
