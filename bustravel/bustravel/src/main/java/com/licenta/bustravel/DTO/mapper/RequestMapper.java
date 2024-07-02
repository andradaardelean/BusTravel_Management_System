package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.RequestDTO;
import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.RequestEntity;
import com.licenta.bustravel.model.enums.RequestStatus;
import com.licenta.bustravel.model.enums.RequestType;
import com.licenta.bustravel.service.implementations.RequestServiceImpl;

import java.util.Map;

public class RequestMapper {
    public static RequestDTO toRequestDTO(RequestEntity request) {
//        if(!request.getStatus().equals(RequestStatus.REJECTED)) {
            Map<String, String> requestDetails = RequestServiceImpl.stringToMap(request.getRequestDetails());
            CompanyEntity companyEntity = CompanyEntity.builder()
                .name(requestDetails.get("name"))
                .description(requestDetails.get("description"))
                .ownerName(requestDetails.get("ownerName"))
                .ownerEmail(requestDetails.get("ownerEmail"))
                .phone(requestDetails.get("phone"))
                .build();
            return RequestDTO.builder()
                .id(request.getId())
                .type(request.getType()
                    .toString())
                .company(CompanyMapper.toDTO(companyEntity))
                .status(request.getStatus()
                    .toString())
                .build();
//        }

//        return RequestDTO.builder()
//            .id(request.getId())
//            .type(request.getType().toString())
//            .company(null)
//            .requestDetails(request.getRequestDetails())
//            .status(request.getStatus().toString())
//            .build();
    }

    public static RequestEntity toRequestEntity(RequestDTO request) {
        return RequestEntity.builder()
            .id(request.getId())
            .type(RequestType.valueOf(request.getType()))
            .requestDetails(null)
            .status(request.getStatus().equals("") ? RequestStatus.PENDING : RequestStatus.valueOf(request.getStatus()))
            .build();
    }

}
