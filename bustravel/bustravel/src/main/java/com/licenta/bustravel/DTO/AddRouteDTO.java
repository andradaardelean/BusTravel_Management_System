package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
@Data
@AllArgsConstructor
public class AddRouteDTO {
    RouteDTO routesDTO;
    ArrayList<StopsDTO> stopsDTOList;
    RecurrenceDTO recurrenceDTO;
}
