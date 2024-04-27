package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.CompanyDTO;
import com.licenta.bustravel.DTO.mapper.CompanyMapper;
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
    public ResponseEntity<?> addCompany(@RequestHeader("Authorization") String authorizationHeader, @RequestBody CompanyDTO companyDTO){
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
    public ResponseEntity<?> getCompanies(@RequestHeader("Authorization") String authorizationHeader){
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

    @GetMapping("/{name}")
    public ResponseEntity<?> getCompanyByName(@RequestHeader("Authorization") String authorizationHeader, @PathVariable String name){
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            CompanyDTO result = CompanyMapper.toDTO(companyService.getByName(name));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid get request!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyCompany(@RequestHeader("Authorization") String authorizationHeader, @PathVariable int id, @RequestBody CompanyDTO companyDTO){
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            CompanyEntity companyEntity = new CompanyEntity(id,companyDTO.getName(), companyDTO.getDescription(), companyDTO.getOwnerName(), companyDTO.getOwnerEmail(), companyDTO.getPhone(), null, null);
            companyService.modify(companyEntity);
            return ResponseEntity.ok("Company modified succesfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid modify request!");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@RequestHeader("Authorization") String authorizationHeader, @PathVariable int id){
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            CompanyEntity companyEntity = companyService.getById(id).orElseThrow();
            companyService.delete(companyEntity);
            return ResponseEntity.ok("Company deleted succesfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid delete request!");
        }
    }

}
