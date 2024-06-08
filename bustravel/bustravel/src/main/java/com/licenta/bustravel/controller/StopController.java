package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.StopsDTO;
import com.licenta.bustravel.DTO.mapper.StopMapper;
import com.licenta.bustravel.service.StopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stops")
@CrossOrigin(origins = "https://travelmanagementsystem.onrender.com/", allowedHeaders = "*")
public class StopController {
    private final StopService stopService;

    @GetMapping
    public ResponseEntity<List<StopsDTO>> getAllStops() {
        return ResponseEntity.ok(StopMapper.toDTOlist(stopService.getAllStops()));
    }

}
