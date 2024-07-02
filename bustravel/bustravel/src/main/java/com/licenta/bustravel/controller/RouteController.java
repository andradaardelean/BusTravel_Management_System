package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.AddRouteDTO;
import com.licenta.bustravel.DTO.LinkDTO;
import com.licenta.bustravel.DTO.RemoveRoutesDTO;
import com.licenta.bustravel.DTO.RouteDTO;
import com.licenta.bustravel.DTO.RouteWithRecurrenceDTO;
import com.licenta.bustravel.DTO.SearchResultDTO;
import com.licenta.bustravel.DTO.mapper.LinkMapper;
import com.licenta.bustravel.DTO.mapper.RouteMapper;
import com.licenta.bustravel.DTO.mapper.StopMapper;
import com.licenta.bustravel.config.OAuthService;
import com.licenta.bustravel.model.LinkEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.model.enums.RecurrenceType;
import com.licenta.bustravel.service.RouteService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("api/routes")
@CrossOrigin(origins = "https://travelmanagementsystem.onrender.com/", allowedHeaders = "*")
public class RouteController {
    private final OAuthService oAuthService;
    private final RouteService routeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class.getName());


    @PostMapping("")
    public ResponseEntity<?> addRoute(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody AddRouteDTO addRouteDTO) {
        try {
            LOGGER.info("Add route request received");
            String token = authorizationHeader.substring(7);
            if (!oAuthService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token invalid!");
            }
            LOGGER.info("Token is valid");
            List<StopEntity> stopEntities = StopMapper.toModelList(addRouteDTO.getStopsDTOList());
            RouteEntity routeEntity = RouteMapper.toModel(addRouteDTO.getRoutesDTO(), addRouteDTO.getRecurrenceDTO()
                .getEveryNo(), addRouteDTO.getRecurrenceDTO()
                .getRecurrenceType());
            LOGGER.info("Route and stops mapped");
            routeService.add(routeEntity, stopEntities, addRouteDTO.getRecurrenceDTO()
                .getDays(), addRouteDTO.getRecurrenceDTO().getEndDate());
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
            if (!oAuthService.isTokenValid(token)) {
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
    public ResponseEntity<?> searchRoutes(@RequestParam(value = "search", required = false) String search,
                                          @RequestParam(value = "startDate", required = false) String startDate,
                                          @RequestParam(value = "endDate", required = false) String endDate,
                                          @RequestParam(value = "startLocation", required = false) String startLocation,
                                          @RequestParam(value = "endLocation", required = false) String endLocation,
                                          @RequestParam(value = "passengersNo", required = false) String passangersNo,
                                          @RequestParam(value = "type", required = false) String type) {
        try {
            Map<List<LinkEntity>, String> allPaths;
            if (type == null || type.equals("all")) {
                allPaths = routeService.search(search, startDate, endDate, startLocation, endLocation, passangersNo);
            } else {
                allPaths = routeService.getShortestPath(search, startDate, endDate, startLocation, endLocation,
                    passangersNo);
            }
            if(allPaths.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            List<SearchResultDTO> result = allPaths.entrySet()
                .stream()
                .map(path -> {
                    List<LinkDTO> links = path.getKey()
                        .stream()
                        .map(LinkMapper::mapToDto)
                        .collect(Collectors.toList());
                    String[] parts = path.getValue()
                        .split(" ");

                    // Reconstruct the distance and duration parts
                    String distance = parts[0] + " " + parts[1];
                    String duration = parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5];
//                    Double price = ;
                    return SearchResultDTO.builder()
                        .links(links)
                        .totalDistance(path.getValue())
                        .totalDistance(distance)
                        .totalTime(duration)
//                        .totalPrice()
                        .build();
                })
                .toList();

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Search failed! " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader("Authorization") String authorizationHeader,
                                     @PathVariable String id) {
        try {
            String token = authorizationHeader.substring(7);
            if (!oAuthService.isTokenValid(token)) {
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
    public ResponseEntity<?> getRoutesForCompany(@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable String company) {
        try {
            String token = authorizationHeader.substring(7);
            if (!oAuthService.isTokenValid(token)) {
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

    //for admin
    @GetMapping()
    public ResponseEntity<List<RouteWithRecurrenceDTO>> getAllRoutes(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            if (!oAuthService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
            }
            List<RouteWithRecurrenceDTO> result = RouteMapper.toDTOListWithRecurrence(routeService.getAll());
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.noContent()
                .build();
        }

    }


}
