package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
public class AddRouteDTO {
    RoutesDTO routesDTO;
    ArrayList<StopsDTO> stopsDTOList;
    RecurrenceDTO recurrenceDTO;
}
