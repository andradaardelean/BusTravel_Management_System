package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.CompanyDTO;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.entities.CompanyEntity;
import com.licenta.bustravel.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/admin")
public class AdminController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/addCompany")
    public @ResponseBody ResponseEntity<?> addCompany(@RequestHeader("Authorization") String authorizationHeader, @RequestBody CompanyDTO companyDTO){
        try{
            String token = authorizationHeader.substring(7);
            if(!jwtService.isTokenValid(token)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            CompanyEntity companyEntity = new CompanyEntity(0,companyDTO.getName(), companyDTO.getDescription(), companyDTO.getOwnerName(), companyDTO.getOwnerEmail(), companyDTO.getPhone(), null, null);
            companyService.add(companyEntity);
            return ResponseEntity.ok("Company added succesfully!");
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid add request!");
        }
    }

}
