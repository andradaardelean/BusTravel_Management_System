package com.licenta.bustravel.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RequestDTO {
    int id;
    String type;
    CompanyDTO company;
    String status;
    String requestDetails;
}
