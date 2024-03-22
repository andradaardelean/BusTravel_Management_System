package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyDTO {
    String name;
    String description;
    String ownerName;
    String ownerEmail;
    String phone;
}
