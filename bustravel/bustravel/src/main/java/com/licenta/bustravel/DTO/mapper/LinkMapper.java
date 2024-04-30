package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.LinkDTO;
import com.licenta.bustravel.model.LinkEntity;

public class LinkMapper {

    public static LinkDTO mapToDto(LinkEntity link){
        return LinkDTO.builder()
            .routeDTO(RouteMapper.toDTO(link.getRoute()))
            .fromStop(StopMapper.toDTO(link.getFromStop()))
            .toStop(StopMapper.toDTO(link.getToStop()))
            .price(link.getPrice())
            .distance(link.getDistanceText())
            .duration(link.getDurationText())
            .build();
    }

    public static LinkEntity mapToModel(LinkDTO linkDTO){
        return LinkEntity.builder()
            .build();
    }
}
