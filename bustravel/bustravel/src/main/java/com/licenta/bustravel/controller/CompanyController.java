package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.CompanyDTO;
import com.licenta.bustravel.DTO.converter.CompanyMapper;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RequestMapping("api/company")
public class CompanyController {
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

    @GetMapping()
    public @ResponseBody ResponseEntity<?> getCompanies(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            List<CompanyDTO> result = CompanyMapper.toDTOList(companyService.getAll());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid get request!");
        }
    }


}
