package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
