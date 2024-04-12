package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.*;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.model.enums.RecurrenceType;
import com.licenta.bustravel.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@RequestMapping("api/routes")
public class RouteController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RouteService routeService;
    private static final Logger LOGGER = Logger.getLogger(RouteController.class.getName());


    @PostMapping("/addRoute")
    public @ResponseBody ResponseEntity<?> addRoute(@RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestBody AddRouteDTO addRouteDTO) {
        try {
            RouteDTO routesDTO = addRouteDTO.getRoutesDTO();
            List<StopsDTO> stopsDTOList = addRouteDTO.getStopsDTOList();
            RecurrenceDTO recurrenceDTO = addRouteDTO.getRecurrenceDTO();
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDate = LocalDateTime.parse(routesDTO.getStartDateTime(), formatter);
            LocalDateTime endDate = LocalDateTime.parse(routesDTO.getEndDateTime(), formatter);
            List<RouteEntity> routes = new ArrayList<>();
            List<Integer> days = recurrenceDTO.getDays();
            Integer everyNo = recurrenceDTO.getEveryNo();
            RecurrenceType recurrenceType = RecurrenceType.valueOf(recurrenceDTO.getRecurrenceType().toString());

            if (recurrenceType == RecurrenceType.NONE) {
                routes.add(
                        new RouteEntity(0, startDate, endDate, routesDTO.getStartLocation(), routesDTO.getEndLocation(),
                                routesDTO.getAvailableSeats(), routesDTO.getTotalSeats(), routesDTO.getPricePerSeat(),
                                null, null, null, null, recurrenceType));
            } else if (recurrenceType == RecurrenceType.DAY) {
                do {
                    routes.add(new RouteEntity(0, startDate, endDate, routesDTO.getStartLocation(),
                            routesDTO.getEndLocation(), routesDTO.getAvailableSeats(), routesDTO.getTotalSeats(),
                            routesDTO.getPricePerSeat(), null, null, null, everyNo, recurrenceType));
                    startDate = startDate.plusDays(everyNo);
                    endDate = endDate.plusDays(everyNo);
                } while (endDate.isBefore(LocalDateTime.parse("2025-01-01T00:00:00")));
            } else if (recurrenceType == RecurrenceType.WEEK) {
                while (!days.isEmpty()) {
                    for (int i = 0; i < 7; i++) {
                        startDate = LocalDateTime.parse(routesDTO.getStartDateTime(),
                                formatter);   //"2024-03-06 8:30" Miercuri 6 martie
                        endDate = LocalDateTime.parse(routesDTO.getEndDateTime(), formatter);

                        Integer currentDay = startDate.getDayOfWeek().getValue() + i - 1; // 2+ i = 7 -> luni
                        if (currentDay > 6) {
                            currentDay = currentDay - 7;    // fac iar luni -> 0
                            startDate = startDate.plusDays(i + 7L * (everyNo - 1));
                            endDate = endDate.plusDays(i + 7L * (everyNo - 1));
                        } else {
                            startDate = startDate.plusDays(i);
                            endDate = endDate.plusDays(i);
                        }
                        if (days.contains(currentDay)) {
                            do {
                                routes.add(new RouteEntity(0, startDate, endDate, routesDTO.getStartLocation(),
                                        routesDTO.getEndLocation(), routesDTO.getAvailableSeats(),
                                        routesDTO.getTotalSeats(), routesDTO.getPricePerSeat(), null, null, null,
                                        everyNo, recurrenceType));
                                startDate = startDate.plusDays(7L + 7L * (everyNo - 1));
                                endDate = endDate.plusDays(7L + 7L * (everyNo - 1));
                            } while (endDate.isBefore(LocalDateTime.parse("2025-01-01T00:00:00")));
                            days.remove(currentDay);
                        }
                    }
                }

            }
            List<StopEntity> stopEntities = new ArrayList<>();
            for (StopsDTO stopDTO : stopsDTOList) {
                StopEntity stop = new StopEntity(stopDTO.getLocation(), stopDTO.getOrder(), stopDTO.getStop(),
                        stopDTO.getStopOrder());
                stopEntities.add(stop);
            }

            routeService.add(routes, stopEntities);
            return ResponseEntity.ok("Route added successfully!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


    @PostMapping("/removeRoute")
    public @ResponseBody ResponseEntity<?> removeRoute(@RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestBody RemoveRoutesDTO routesDTO) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDate = LocalDateTime.parse(routesDTO.getRoutesDTO().getStartDateTime(), formatter);
            LocalDateTime endDate = LocalDateTime.parse(routesDTO.getRoutesDTO().getEndDateTime(), formatter);
            RouteEntity routeEntity = new RouteEntity(0, startDate, endDate,
                    routesDTO.getRoutesDTO().getStartLocation(), routesDTO.getRoutesDTO().getEndLocation(),
                    routesDTO.getRoutesDTO().getAvailableSeats(), routesDTO.getRoutesDTO().getTotalSeats(),
                    routesDTO.getRoutesDTO().getPricePerSeat(), null, null, null, null, null);
            routeService.delete(routeEntity, routesDTO.getRemoveAllRecursive());
            return ResponseEntity.ok("All routes deleted!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete failed!");
        }
    }

    @GetMapping("/search")
    public @ResponseBody ResponseEntity<?> searchRoutes(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestParam("search") String search,
                                                        @RequestParam("startDate") String startDate,
                                                        @RequestParam("endDate") String endDate,
                                                        @RequestParam("startLocation") String startLocation,
                                                        @RequestParam("endLocation") String endLocation,
                                                        @RequestParam("passengersNo") String passangersNo) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            List<RouteEntity> routeEntities = routeService.search(search, startDate, endDate, startLocation, endLocation,
                    passangersNo);
            LOGGER.info("dupa search ");
            List<RouteDTO> result = new ArrayList<>();
            for (RouteEntity route : routeEntities) {
                result.add(new RouteDTO(route.getId(), route.getStartDateTime().toString(),
                        route.getEndDateTime().toString(), route.getStartLocation(), route.getEndLocation(),
                        route.getAvailableSeats(), route.getPrice(), route.getTotalSeats()));
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Search failed!");
        }
    }

    @GetMapping("/get")
    public @ResponseBody ResponseEntity<?> getById(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestParam String id) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            Optional<RouteEntity> routeEntity = routeService.getById(Integer.parseInt(id));

            if (routeEntity.isPresent()) {
                RouteEntity routeEntity1 = routeEntity.get();
                return new ResponseEntity<>(
                        new RouteDTO(routeEntity1.getId(), routeEntity1.getStartDateTime().toString(),
                                routeEntity1.getEndDateTime().toString(), routeEntity1.getStartLocation(),
                                routeEntity1.getEndLocation(), routeEntity1.getAvailableSeats(),
                                routeEntity1.getPrice(), routeEntity1.getTotalSeats()), HttpStatus.OK);
            } else {
                return ResponseEntity.ok("There is no route with this id!");
            }
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get by id failed!");
        }
    }

    @GetMapping("/forCompany/{company}")
    public @ResponseBody ResponseEntity<?> getRoutesForCompany(@RequestHeader("Authorization") String authorizationHeader,
                                                               @PathVariable String company) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid!");
            }
            List<RouteEntity> routeEntities = routeService.getRoutesForCompany(company);
            List<RouteDTO> result = new ArrayList<>();
            for (RouteEntity route : routeEntities) {
                result.add(new RouteDTO(route.getId(), route.getStartDateTime().toString(),
                        route.getEndDateTime().toString(), route.getStartLocation(), route.getEndLocation(),
                        route.getAvailableSeats(), route.getPrice(), route.getTotalSeats()));
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get routes for company failed!");
        }
    }

}
