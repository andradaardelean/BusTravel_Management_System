package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.RouteDTO;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.enums.RecurrenceType;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RouteMapper {
    public static RouteDTO toDTO(RouteEntity route) {
        String startDate = route.getStartDateTime().toString().split("T")[1].length() == 5 ? route.getStartDateTime() + ":00" : route.getStartDateTime().toString();
        String endDate = route.getEndDateTime().toString().split("T")[1].length() == 5 ? route.getEndDateTime() + ":00" : route.getEndDateTime().toString();
        return RouteDTO.builder()
            .id(route.getId())
            .startDateTime(startDate)
            .endDateTime(endDate)
            .startLocation(route.getStartLocation())
            .endLocation(route.getEndLocation())
            .availableSeats(route.getAvailableSeats())
            .pricePerSeat(route.getPrice())
            .totalSeats(route.getTotalSeats())
            .build();
    }

    public static RouteEntity toModel(RouteDTO route, Integer reccurencyNo, RecurrenceType recurrenceType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
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
            .links(new ArrayList<>())
            .build();
    }

    public static List<RouteDTO> toDTOList(List<RouteEntity> routes) {
        return routes.stream()
            .map(RouteMapper::toDTO)
            .toList();
    }
}
