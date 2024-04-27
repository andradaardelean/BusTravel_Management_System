package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.*;
import com.licenta.bustravel.DTO.mapper.RouteMapper;
import com.licenta.bustravel.DTO.mapper.StopMapper;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.model.enums.RecurrenceType;
import com.licenta.bustravel.service.RouteService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RequestMapping("api/routes")
public class RouteController {
    private final JwtService jwtService;
    private final RouteService routeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class.getName());


    @PostMapping("")
    public ResponseEntity<?> addRoute(@RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestBody AddRouteDTO addRouteDTO) {
        try {
            LOGGER.info("Add route request received");
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token invalid!");
            }
            LOGGER.info( "Token is valid");
            List<StopEntity> stopEntities = StopMapper.toModelList(addRouteDTO.getStopsDTOList());
            RouteEntity routeEntity = RouteMapper.toModel(addRouteDTO.getRoutesDTO(), addRouteDTO.getRecurrenceDTO().getEveryNo(),
                addRouteDTO.getRecurrenceDTO().getRecurrenceType());
            LOGGER.info("Route and stops mapped");
            routeService.add(routeEntity, stopEntities, addRouteDTO.getRecurrenceDTO().getDays());
            LOGGER.info("Route added successfully");
            return ResponseEntity.ok("Route added successfully!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
        }
    }


    @DeleteMapping("")
    public ResponseEntity<?> removeRoute(@RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestBody RemoveRoutesDTO routesDTO) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token invalid!");
            }
            RouteEntity routeEntity = RouteMapper.toModel(routesDTO.getRoutesDTO(), null, RecurrenceType.NONE);
            routeService.delete(routeEntity, routesDTO.getRemoveAllRecursive());
            return ResponseEntity.ok("All routes deleted!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Delete failed!");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRoutes(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestParam("search") String search,
                                                        @RequestParam("startDate") String startDate,
                                                        @RequestParam("endDate") String endDate,
                                                        @RequestParam("startLocation") String startLocation,
                                                        @RequestParam("endLocation") String endLocation,
                                                        @RequestParam("passengersNo") String passangersNo) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token invalid!");
            }
            Map<List<StopEntity>, String> allPaths = routeService.search(search, startDate, endDate, startLocation,
                endLocation, passangersNo);
            Map<List<StopsDTO>, String> result = new HashMap<>();
            //
            for (Map.Entry<List<StopEntity>, String> path : allPaths.entrySet()) {
                var stopsDTO = StopMapper.toDTOlist(path.getKey());
                result.put(stopsDTO, path.getValue());
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Search failed!");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getById(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestParam String id) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token invalid!");
            }
            RouteEntity routeEntity = routeService.getById(Integer.parseInt(id))
                .orElseThrow(() -> new RuntimeException("Route not found!"));
            return new ResponseEntity<>(RouteMapper.toDTO(routeEntity), HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Get by id failed!");
        }

    }

    @GetMapping("/forCompany/{company}")
    public ResponseEntity<?> getRoutesForCompany(
        @RequestHeader("Authorization") String authorizationHeader, @PathVariable String company) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token invalid!");
            }
            List<RouteDTO> result = RouteMapper.toDTOList(routeService.getRoutesForCompany(company));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Get routes for company failed!");
        }
    }

}
