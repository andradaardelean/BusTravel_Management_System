package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.RequestDTO;
import com.licenta.bustravel.model.RequestEntity;
import com.licenta.bustravel.model.enums.RequestType;

public class RequestMapper {
    public static RequestDTO toRequestDTO(RequestEntity request) {
        return RequestDTO.builder()
            .id(request.getId())
            .type(request.getType().toString())
            .company(null)
            .build();
    }

    public static RequestEntity toRequestEntity(RequestDTO request) {
        return RequestEntity.builder()
            .id(request.getId())
            .type(RequestType.valueOf(request.getType()))
            .requestDetails(null)
            .build();
    }

}
