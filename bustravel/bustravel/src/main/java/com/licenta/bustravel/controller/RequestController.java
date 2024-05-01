package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.RequestDTO;
import com.licenta.bustravel.DTO.mapper.CompanyMapper;
import com.licenta.bustravel.DTO.mapper.RequestMapper;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.enums.RequestType;
import com.licenta.bustravel.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests")
public class RequestController {
    private final RequestService requestService;
    private final JwtService jwtService;


    // NOT TESTED
    @GetMapping
    public ResponseEntity<List<RequestDTO>> getAllRequests(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(requestService.getAllRequests()
            .stream()
            .map(RequestMapper::toRequestDTO)
            .toList());
    }

    @PostMapping
    public ResponseEntity<?> makeRequest(@RequestHeader("Authorization") String authorizationHeader,@RequestBody RequestDTO requestDTO) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token already invalidated!");
            if(RequestType.valueOf(requestDTO.getType()).equals(RequestType.COMPANY_APPLICATION)) {
                requestService.makeCompanyRequest(RequestMapper.toRequestEntity(requestDTO), CompanyMapper.toModel(requestDTO.getCompany()));
                return ResponseEntity.created(new URI("/api/requests")).build();
            }
            return ResponseEntity.badRequest().body("Request type not supported.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Add request does not work. " + e.getMessage());
        }
    }
}
