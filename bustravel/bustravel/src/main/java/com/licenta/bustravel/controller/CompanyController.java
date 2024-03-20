package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.*;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.entities.CompanyEntity;
import com.licenta.bustravel.entities.RouteEntity;
import com.licenta.bustravel.entities.StopEntity;
import com.licenta.bustravel.service.RouteService;
import com.licenta.bustravel.service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@RestController
@CrossOrigin
@RequestMapping("api/company")
public class CompanyController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RouteService routeService;
    private static final Logger LOGGER = Logger.getLogger(CompanyController.class.getName());


    @PostMapping("/addRoute")
    public @ResponseBody ResponseEntity<?> addRoute(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AddRouteDTO addRouteDTO){
        try{
            RoutesDTO routesDTO = addRouteDTO.getRoutesDTO();
            List<StopsDTO> stopsDTOList = addRouteDTO.getStopsDTOList();
            RecurrenceDTO recurrenceDTO = addRouteDTO.getRecurrenceDTO();
            String token = authorizationHeader.substring(7);
            if(!jwtService.isTokenValid(token)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDate = LocalDateTime.parse(routesDTO.getStartDateTime(), formatter);
            LocalDateTime endDate = LocalDateTime.parse(routesDTO.getEndDateTime(), formatter);
            List<RouteEntity> routes = new ArrayList<>();
            List<Integer> days = recurrenceDTO.getDays();
            Integer everyNo = recurrenceDTO.getEveryNo();
            RecurrenceType recurrenceType = RecurrenceType.valueOf(recurrenceDTO.getRecurrenceType());
            LOGGER.info("NUJ 1");
            if(recurrenceType == RecurrenceType.NONE) {
                routes.add(new RouteEntity(0,startDate, endDate,routesDTO.getStartLocation(), routesDTO.getEndLocation(), routesDTO.getAvailableSeats(),  routesDTO.getTotalSeats(),routesDTO.getPricePerSeat(),null, null,null));
            }

            else if(recurrenceType == RecurrenceType.DAY){
                do{
                    routes.add(new RouteEntity(0,startDate, endDate,routesDTO.getStartLocation(), routesDTO.getEndLocation(), routesDTO.getAvailableSeats(),  routesDTO.getTotalSeats(),routesDTO.getPricePerSeat(),null, null,null));
                    startDate= startDate.plusDays(everyNo);
                    endDate = endDate.plusDays(everyNo);
                }while(endDate.isBefore(LocalDateTime.parse("2025-01-01T00:00:00")));
            }else if(recurrenceType == RecurrenceType.WEEK){
                while(!days.isEmpty()) {
//                    LOGGER.info("NUJ 2");
                    for(int i=0;i<7;i++) {
                        startDate = LocalDateTime.parse(routesDTO.getStartDateTime(), formatter);   //"2024-03-06 8:30" Miercuri 6 martie
                        endDate = LocalDateTime.parse(routesDTO.getEndDateTime(), formatter);
                        Integer currentDay = startDate.getDayOfWeek().getValue()+i; // 2+ i = 7 -> luni
                        if(currentDay > 6){
                            currentDay = currentDay - 7;    // fac iar luni -> 0
                        }
                        startDate = startDate.plusDays(i);
                        if (days.contains(currentDay)) {
                            do {
//                                LOGGER.info("NUJ 3");
                                routes.add(new RouteEntity(0, startDate, endDate, routesDTO.getStartLocation(), routesDTO.getEndLocation(), routesDTO.getAvailableSeats(), routesDTO.getTotalSeats(), routesDTO.getPricePerSeat(), null, null,null));
                                startDate = startDate.plusDays(7);
                                endDate = endDate.plusDays(7);
                            } while (endDate.isBefore(LocalDateTime.parse("2025-01-01T00:00:00")));
                            days.remove(currentDay);
                        }
                    }
                }

            }
            List<StopEntity> stopEntities = new ArrayList<>();
            for(StopsDTO stopDTO : stopsDTOList){
                StopEntity stop = new StopEntity(0,stopDTO.getLocation(),stopDTO.getOrder(), stopDTO.getStop(), stopDTO.getStopOrder(),null);
                stopEntities.add(stop);
            }

            routeService.add(routes, stopEntities);
            return ResponseEntity.ok("Route added successfully!");
        }catch(Exception ex ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
