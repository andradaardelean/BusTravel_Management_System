package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StopsDTO {
    String location;
    Integer order;
    String stop;
    Integer stopOrder;
}
