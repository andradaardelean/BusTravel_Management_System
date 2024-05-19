package com.licenta.bustravel.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LinkDTO {
    RouteDTO routeDTO;
    StopsDTO fromStop;
    StopsDTO toStop;
    String distance;
    String duration;
    Double price;
    String startTime;
    String endTime;
    int order;

}
