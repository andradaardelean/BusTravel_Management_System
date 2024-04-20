package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.CompanyDTO;
import com.licenta.bustravel.model.CompanyEntity;

import java.util.List;

public class CompanyMapper {

    public static CompanyDTO toDTO(CompanyEntity company) {
        return CompanyDTO.builder()
            .id(company.getId())
            .name(company.getName())
            .description(company.getDescription())
            .ownerEmail(company.getOwnerEmail())
            .ownerName(company.getOwnerName())
            .phone(company.getPhone())
            .build();
    }

    public static CompanyEntity toModel(CompanyDTO company) {
        return CompanyEntity.builder()
            .id(company.getId())
            .name(company.getName())
            .description(company.getDescription())
            .ownerEmail(company.getOwnerEmail())
            .ownerName(company.getOwnerName())
            .phone(company.getPhone())
            .build();
    }

    public static List<CompanyDTO> toDTOList(List<CompanyEntity> companies) {
        return companies.stream()
            .map(CompanyMapper::toDTO)
            .toList();
    }
}
