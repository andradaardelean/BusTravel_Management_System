package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.StopsDTO;
import com.licenta.bustravel.model.StopEntity;

import java.util.ArrayList;
import java.util.List;

public class StopMapper {
    public static StopsDTO toDTO(StopEntity stop) {
        return StopsDTO.builder()
                .location(stop.getLocation())
                .address(stop.getAddress())
                .build();
    }

    public static StopEntity toModel(StopsDTO stop) {
        return StopEntity.builder()
                .location(stop.getLocation())
                .address(stop.getAddress())
                .fromLinks(new ArrayList<>())
                .toLinks(new ArrayList<>())
                .build();
    }

    public static List<StopsDTO> toDTOlist(List<StopEntity> stops) {
        return stops.stream()
                .map(StopMapper::toDTO)
                .toList();
    }

    public static List<StopEntity> toModelList(List<StopsDTO> stops) {
        return stops.stream()
                .map(StopMapper::toModel)
                .toList();
    }
}
