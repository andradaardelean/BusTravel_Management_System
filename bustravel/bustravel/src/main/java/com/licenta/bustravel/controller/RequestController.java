package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.RequestDTO;
import com.licenta.bustravel.DTO.mapper.CompanyMapper;
import com.licenta.bustravel.DTO.mapper.RequestMapper;
import com.licenta.bustravel.config.OAuthService;
import com.licenta.bustravel.model.enums.RequestType;
import com.licenta.bustravel.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests")
@CrossOrigin(origins = "https://travelmanagementsystem.onrender.com/", allowedHeaders = "*")
public class RequestController {
    private final RequestService requestService;
    private final OAuthService oAuthService;
    private final OAuthService auth0Service;

    private final Logger LOGGER = LoggerFactory.getLogger(RequestController.class.getName());

    @GetMapping("/{status}")
    public ResponseEntity<?> getAllRequests(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String status) {
        try {
            String token = authorizationHeader.substring(7);
            if (!oAuthService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token already invalidated!");
            String subjectId = auth0Service.getOAuthId();
            LOGGER.info("SubjectId: " + subjectId);
            return ResponseEntity.ok(requestService.getAllRequests(status)
                .stream()
                .map(RequestMapper::toRequestDTO)
                .toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Get all requests does not work. " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> makeRequest(@RequestBody RequestDTO requestDTO) {
        try {
            if(RequestType.valueOf(requestDTO.getType()).equals(RequestType.COMPANY_APPLICATION)) {
                requestService.makeCompanyRequest(RequestMapper.toRequestEntity(requestDTO), CompanyMapper.toModel(requestDTO.getCompany()));
                return ResponseEntity.created(new URI("/api/requests")).build();
            }
            return ResponseEntity.badRequest().body("Request type not supported.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Add request does not work. " + e.getMessage());
        }
    }

    @PostMapping("/solve")
    public ResponseEntity<?> solveRequest(@RequestHeader("Authorization") String authorizationHeader, @RequestBody RequestDTO requestDTO) {
        try {
            String token = authorizationHeader.substring(7);
            if (!oAuthService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token already invalidated!");
            requestService.solveCompanyRequest(RequestMapper.toRequestEntity(requestDTO));
            return ResponseEntity.ok("Request resolved.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Resolve request does not work. " + e.getMessage());
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectRequest(@RequestHeader("Authorization") String authorizationHeader, @RequestBody RequestDTO requestDTO) {
        try {
            String token = authorizationHeader.substring(7);
            if (!oAuthService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token already invalidated!");
            requestService.rejectCompanyRequest(RequestMapper.toRequestEntity(requestDTO));
            return ResponseEntity.ok("Request rejected.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Reject request does not work. " + e.getMessage());
        }
    }

}
