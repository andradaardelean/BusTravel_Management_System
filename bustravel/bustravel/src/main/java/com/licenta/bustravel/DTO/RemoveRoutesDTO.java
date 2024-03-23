package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveRoutesDTO {
    RouteDTO routesDTO;
    Boolean removeAllRecursive;
}
