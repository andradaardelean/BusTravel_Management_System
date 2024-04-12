package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CompanyDTO {
    String name;
    String description;
    String ownerName;
    String ownerEmail;
    String phone;
}
