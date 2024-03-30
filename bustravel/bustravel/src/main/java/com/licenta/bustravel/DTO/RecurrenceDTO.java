package com.licenta.bustravel.DTO;

import com.licenta.bustravel.model.enums.RecurrenceType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
    @AllArgsConstructor
    public class RecurrenceDTO {
        List<Integer> days;
        Integer everyNo;
        RecurrenceType recurrenceType;
}
