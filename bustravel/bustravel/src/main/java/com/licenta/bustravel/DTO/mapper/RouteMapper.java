package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.RouteDTO;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.enums.RecurrenceType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RouteMapper {
    public static RouteDTO toDTO(RouteEntity route) {
        return RouteDTO.builder()
            .id(route.getId())
            .startDateTime(route.getStartDateTime().toString())
            .endDateTime(route.getEndDateTime().toString())
            .startLocation(route.getStartLocation())
            .endLocation(route.getEndLocation())
            .availableSeats(route.getAvailableSeats())
            .pricePerSeat(route.getPrice())
            .totalSeats(route.getTotalSeats())
            .build();
    }

    public static RouteEntity toModel(RouteDTO route, Integer reccurencyNo, RecurrenceType recurrenceType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return RouteEntity.builder()
            .id(route.getId())
            .startDateTime(LocalDateTime.parse(route.getStartDateTime(), formatter))
            .endDateTime(LocalDateTime.parse(route.getEndDateTime(), formatter))
            .startLocation(route.getStartLocation())
            .endLocation(route.getEndLocation())
            .availableSeats(route.getAvailableSeats())
            .price(route.getPricePerSeat())
            .totalSeats(route.getTotalSeats())
            .reccurencyNo(reccurencyNo)
            .recurrenceType(recurrenceType)
            .bookingList(new ArrayList<>())
            .links(new ArrayList<>())
            .build();
    }

    public static List<RouteDTO> toDTOList(List<RouteEntity> routes) {
        return routes.stream()
            .map(RouteMapper::toDTO)
            .toList();
    }
}
