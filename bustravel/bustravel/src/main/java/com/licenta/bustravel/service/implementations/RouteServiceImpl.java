package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.controller.RouteController;
import com.licenta.bustravel.model.*;
import com.licenta.bustravel.model.enums.RecurrenceType;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.*;
import com.licenta.bustravel.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    private static final Logger LOGGER = Logger.getLogger(RouteController.class.getName());
    @Autowired
    UserRepository userRepository;
    @Autowired
    StopsRepository stopsRepository;
    @Autowired
    IntermediateRouteRepository intermediateRouteRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Override
    public void add(List<RouteEntity> routeEntities, List<StopEntity> stops) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username).get();
        if (userCurrent.getUserType().equals(UserType.CLIENT)) {
            throw new Exception("Not allowed.");
        }
        try {
            CompanyEntity companyEntity = userCurrent.getCompanyEntity();
            List<RouteEntity> sortedRoutes = new ArrayList<>(routeEntities);
            sortedRoutes.forEach(routeEntity -> routeEntity.setCompanyEntity(companyEntity));

            sortedRoutes = sortedRoutes.stream()
                    .sorted(Comparator.comparing(RouteEntity::getStartDateTime))
                    .collect(Collectors.toList());
            routeRepository.saveAll(sortedRoutes);
            for (StopEntity stop : stops) {
                StopEntity stop1 = stopsRepository.findStop(stop.getLocation(), stop.getOrder(), stop.getStop(),
                        stop.getStopOrder());
                if (stop1 == null) {
                    stopsRepository.save(stop);
                } else {
                    stop.setId(stop1.getId());
                }
            }
            List<IntermediateRoutesEntity> intermediateRoutes = routeEntities.stream()
                    .flatMap(route -> stops.stream().map(stop -> {
                        IntermediateRoutesEntity intermediateRoute = new IntermediateRoutesEntity();
                        intermediateRoute.setId(0);
                        intermediateRoute.setRouteId(route.getId());
                        intermediateRoute.setStopId(stop.getId());
                        return intermediateRoute;
                    }))
                    .collect(Collectors.toList());
            intermediateRouteRepository.saveAll(intermediateRoutes);
        } catch (Exception ex) {
            throw new Exception("Add failed!");
        }
    }

    @Override
    public Optional getById(int id) throws Exception {
        return routeRepository.findById(id);
    }

    @Override
    public void modify(RouteEntity routeEntity, List<StopEntity> stops) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username).get();
        if (userCurrent.getUserType().equals(UserType.CLIENT)) {
            throw new Exception("Not allowed.");
        }

        try {
            routeRepository.save(routeEntity);
            // addauga save la stops
//            for (StopEntity stop : stops) {
//                stop.setRouteEntity(routeEntity);
//                stopsRepository.save(stop);
//            }
        } catch (Exception e) {
            throw new Exception("Modify failed!");
        }
    }

    public List<RouteEntity> getAllRoutesToDelete(RouteEntity routeEntity) throws Exception {
        List<RouteEntity> result = new ArrayList<>();
        LocalDateTime currentStart = routeEntity.getStartDateTime();
        LocalDateTime currentEnd = routeEntity.getEndDateTime();
        do {
            RouteEntity routeEntity1 = routeRepository.findRoute(currentStart, currentEnd,
                    routeEntity.getStartLocation(), routeEntity.getEndLocation());
            if (routeEntity1 == null) {
                throw new Exception("Route entity does not exist!");
            }
            result.add(routeEntity1);
            if (routeEntity.getRecurrenceType() == RecurrenceType.DAY) {
                currentStart = currentStart.plusDays(routeEntity.getReccurencyNo());
                currentEnd = currentEnd.plusDays(routeEntity.getReccurencyNo());
            } else {
                currentStart = currentStart.plusDays(7L * routeEntity.getReccurencyNo());
                currentEnd = currentEnd.plusDays(7L * routeEntity.getReccurencyNo());
            }

        } while (currentEnd.isBefore((LocalDateTime.parse("2025-01-01T00:00:00"))));
        return result;
    }

    @Override
    public void delete(RouteEntity routeEntity, Boolean removeAll) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username).get();
        if (userCurrent.getUserType().equals(UserType.CLIENT)) {
            throw new Exception("Not allowed.");
        }
        RouteEntity routeToRemove = routeRepository.findRoute(routeEntity.getStartDateTime(),
                routeEntity.getEndDateTime(), routeEntity.getStartLocation(), routeEntity.getEndLocation());
        if (routeToRemove == null) {
            throw new Exception("Route not found!");
        }
        try {
            if (Boolean.TRUE.equals(removeAll)) {
                if (routeToRemove.getRecurrenceType() != RecurrenceType.NONE) {
                    List<RouteEntity> routesToDelete = getAllRoutesToDelete(routeToRemove);
                    for (RouteEntity routeEntity1 : routesToDelete) {
                        List<IntermediateRoutesEntity> toDelete = intermediateRouteRepository.findAll()
                                .stream()
                                .filter(intermediateRoutesEntity -> intermediateRoutesEntity.getRouteId() == routeEntity1.getId())
                                .toList();
                        intermediateRouteRepository.deleteAll(toDelete);
                        routeRepository.delete(routeEntity1);
                    }
                } else {
                    throw new Exception("There is no recurrence for this route");
                }
            } else {
                List<IntermediateRoutesEntity> toDelete = intermediateRouteRepository.findAll()
                        .stream()
                        .filter(intermediateRoutesEntity -> intermediateRoutesEntity.getRouteId() == routeToRemove.getId())
                        .toList();
                intermediateRouteRepository.deleteAll(toDelete);
                routeRepository.delete(routeToRemove);
            }
        } catch (Exception e) {
            throw new Exception("Delete failed!");
        }
    }

    @Override
    public List<RouteEntity> getAll() throws Exception {
        return routeRepository.findAll();
    }

    @Override
    public List<RouteEntity> search(String search, String startDate, String endDate, String startLocation, String endLocation,
                                    String passangersNo) throws Exception {
        List<RouteEntity> foundRoutes;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startdate = LocalDate.parse(startDate, dateTimeFormatter);
        if (Objects.equals(endDate, "null")) {
            foundRoutes = routeRepository.findAll()
                    .stream()
                    .filter(route -> route.getStartDateTime().toLocalDate().isEqual(startdate))
                    .toList();
        } else {
            LocalDate enddate = LocalDate.parse(endDate, dateTimeFormatter);
            foundRoutes = routeRepository.findAll()
                    .stream()
                    .filter(routeEntity -> (routeEntity.getStartDateTime()
                            .toLocalDate()
                            .isAfter(startdate) || routeEntity.getStartDateTime()
                            .toLocalDate()
                            .isEqual(startdate)) && (routeEntity.getStartDateTime()
                            .toLocalDate()
                            .isBefore(enddate) || routeEntity.getStartDateTime().toLocalDate().isEqual(enddate)))
                    .toList();
        }
        if (!Objects.equals(startLocation, "null")) {
            foundRoutes = foundRoutes.stream()
                    .filter(routeEntity -> routeEntity.getStartLocation().equals(startLocation))
                    .toList();
        }
        if (!Objects.equals(endLocation, "null")) {

            foundRoutes = foundRoutes.stream()
                    .filter(routeEntity -> routeEntity.getEndLocation().equals(endLocation))
                    .toList();
        }
        if (!Objects.equals(passangersNo, "null")) {
            Integer passangers = Integer.parseInt(passangersNo);
            foundRoutes = foundRoutes.stream()
                    .filter(routeEntity -> routeEntity.getAvailableSeats() > passangers)
                    .toList();
        }
        return foundRoutes;
    }

    public List<StopEntity> getAllStops() {
        return stopsRepository.findAll();
    }

    public List<StopEntity> getStopsForRoute(RouteEntity routeEntity) {
        List<StopEntity> stops = new ArrayList<>();
        for (StopEntity stop : stopsRepository.findAll()) {
            stops.add(stop);
        }
        return stops;
    }
}
