package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
    @AllArgsConstructor
    public class RecurrenceDTO {
        List<Integer> days;
        Integer everyNo;
        String recurrenceType;
}
