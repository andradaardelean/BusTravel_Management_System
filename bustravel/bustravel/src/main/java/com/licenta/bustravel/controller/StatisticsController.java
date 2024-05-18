package com.licenta.bustravel.controller;

import com.licenta.bustravel.config.OAuthService;
import com.licenta.bustravel.service.statistics.AdminStatistics;
import com.licenta.bustravel.service.statistics.CompanyStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/statistics")
@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final AdminStatistics adminStatistics;
    private final CompanyStatistics companyStatistics;
    private final OAuthService oAuthService;

    @GetMapping("/admin")
    public ResponseEntity<?> getAdminStatistics(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7);
            if(!oAuthService.isTokenValid(token))
                return ResponseEntity.badRequest().body("Invalid token");
            return ResponseEntity.ok(adminStatistics.getStatistics());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/company")
    public ResponseEntity<?> getCompanyStatistics(@RequestHeader("Authorization") String authorization, @RequestParam String company) {
        try {
            String token = authorization.substring(7);
            if(!oAuthService.isTokenValid(token))
                return ResponseEntity.badRequest().body("Invalid token");
            return ResponseEntity.ok(companyStatistics.getStatistics(company));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

