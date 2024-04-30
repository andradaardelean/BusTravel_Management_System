package com.licenta.bustravel.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class SearchResultDTO {
    List<LinkDTO> links;
    String totalDistance;
    String totalTime;
    Double totalPrice;
}
