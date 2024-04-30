package com.licenta.bustravel.service.utils;

import com.licenta.bustravel.model.RouteEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Link {
    Node destination;
    RouteEntity route;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
}
