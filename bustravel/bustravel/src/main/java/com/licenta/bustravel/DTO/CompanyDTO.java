package com.licenta.bustravel.DTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@Builder
public class CompanyDTO {
    int id;
    String name;
    String description;
    String ownerName;
    String ownerEmail;
    String phone;
}
