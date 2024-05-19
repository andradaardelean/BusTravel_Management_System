package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.LinkDTO;
import com.licenta.bustravel.model.LinkEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LinkMapper {

    public static LinkDTO mapToDto(LinkEntity link){
        String startDate = link.getStartTime().toString().split("T")[1].length() == 5 ? link.getStartTime() + ":00" : link.getStartTime().toString();
        String endDate = link.getEndTime().toString().split("T")[1].length() == 5 ? link.getEndTime() + ":00" : link.getEndTime().toString();

        return LinkDTO.builder()
            .routeDTO(RouteMapper.toDTO(link.getRoute()))
            .fromStop(StopMapper.toDTO(link.getFromStop()))
            .toStop(StopMapper.toDTO(link.getToStop()))
            .price(link.getPrice())
            .distance(link.getDistanceText())
            .duration(link.getDurationText())
            .startTime(startDate)
            .endTime(endDate)
            .build();
    }

    public static LinkEntity mapToModel(LinkDTO linkDTO){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LinkEntity.builder()
            .route(RouteMapper.toModel(linkDTO.getRouteDTO(), 0, null))
            .fromStop(StopMapper.toModel(linkDTO.getFromStop()))
            .toStop(StopMapper.toModel(linkDTO.getToStop()))
            .price(linkDTO.getPrice())
            .distanceText(linkDTO.getDistance())
            .durationText(linkDTO.getDuration())
            .order(linkDTO.getOrder())
            .startTime(LocalDateTime.parse(linkDTO.getStartTime(), formatter))
            .endTime(LocalDateTime.parse(linkDTO.getEndTime(), formatter))
            .build();
    }
}
