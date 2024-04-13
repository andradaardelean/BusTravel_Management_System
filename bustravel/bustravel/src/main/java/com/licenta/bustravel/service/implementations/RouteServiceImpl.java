package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.DTO.StopsDTO;
import com.licenta.bustravel.DTO.converter.RouteMapper;
import com.licenta.bustravel.controller.RouteController;
import com.licenta.bustravel.model.*;
import com.licenta.bustravel.model.enums.RecurrenceType;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.*;
import com.licenta.bustravel.service.RouteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

@Service
public class RouteServiceImpl implements RouteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class.getName());
    @Autowired
    UserRepository userRepository;
    @Autowired
    StopsRepository stopsRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    private RouteRepository routeRepository;

    public UserEntity validateUserType() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username)
                .get();
        if (userCurrent.getUserType()
                .equals(UserType.CLIENT)) {
            throw new Exception("Not allowed.");
        }
        return userCurrent;
    }

    public List<RouteEntity> generateForWeekReccurency(RouteEntity route, List<Integer> days, Integer everyNo) {
        List<RouteEntity> routes = new ArrayList<>();
        LocalDateTime initialStartDate = route.getStartDateTime();
        LocalDateTime initialEndDate = route.getEndDateTime();
        while (!days.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                LocalDateTime startDate = initialStartDate;
                LocalDateTime endDate = initialEndDate;

                Integer currentDay = startDate.getDayOfWeek()
                    .getValue() + i - 1;
                if (currentDay > 6) {
                    currentDay = currentDay - 7;
                    startDate = startDate.plusDays(i + 7L * (everyNo - 1));
                    endDate = endDate.plusDays(i + 7L * (everyNo - 1));
                } else {
                    startDate = startDate.plusDays(i);
                    endDate = endDate.plusDays(i);
                }
                if (days.contains(currentDay)) {
                    do {
                        RouteEntity newRoute = RouteEntity.builder()
                            .startDateTime(LocalDateTime.of(startDate.toLocalDate(), startDate.toLocalTime()))
                            .endDateTime(LocalDateTime.of(endDate.toLocalDate(), endDate.toLocalTime()))
                            .startLocation(route.getStartLocation())
                            .endLocation(route.getEndLocation())
                            .availableSeats(route.getAvailableSeats())
                            .price(route.getPrice())
                            .totalSeats(route.getTotalSeats())
                            .reccurencyNo(route.getReccurencyNo())
                            .recurrenceType(route.getRecurrenceType())
                            .stopEntities(route.getStopEntities())
                            .companyEntity(route.getCompanyEntity())
                            .build();
                        routes.add(newRoute);
                        startDate = startDate.plusDays(7L + 7L * (everyNo - 1));
                        endDate = endDate.plusDays(7L + 7L * (everyNo - 1));
                    } while (endDate.isBefore(LocalDateTime.parse("2025-01-01T00:00:00")));
                    days.remove(currentDay);
                }
            }
        }
        return routes;
    }

    public List<RouteEntity> generateForDayReccurency(RouteEntity route, Integer everyNo, LocalDateTime startDate, LocalDateTime endDate) {
        List<RouteEntity> routes = new ArrayList<>();
        do {
            RouteEntity newRoute = RouteEntity.builder()
                .startDateTime(LocalDateTime.of(startDate.toLocalDate(), startDate.toLocalTime()))
                .endDateTime(LocalDateTime.of(endDate.toLocalDate(), endDate.toLocalTime()))
                .startLocation(route.getStartLocation())
                .endLocation(route.getEndLocation())
                .availableSeats(route.getAvailableSeats())
                .price(route.getPrice())
                .totalSeats(route.getTotalSeats())
                .reccurencyNo(route.getReccurencyNo())
                .recurrenceType(route.getRecurrenceType())
                .stopEntities(route.getStopEntities())
                .companyEntity(route.getCompanyEntity())
                .build();
            routes.add(newRoute);
            startDate = startDate.plusDays(everyNo);
            endDate = endDate.plusDays(everyNo);
        } while (endDate.isBefore(LocalDateTime.parse("2025-01-01T00:00:00")));
        return routes;
    }

    public List<RouteEntity> generateRoutes(RouteEntity route, List<Integer> days) {
        LocalDateTime startDate = route.getStartDateTime();
        LocalDateTime endDate = route.getEndDateTime();
        List<RouteEntity> routes = new ArrayList<>();
        Integer everyNo = route.getReccurencyNo();
        RecurrenceType recurrenceType = RecurrenceType.valueOf(route.getRecurrenceType()
            .toString());
        if (recurrenceType == RecurrenceType.NONE) {
            LOGGER.info("No recurrence");
            routes.add(route);
        } else if (recurrenceType == RecurrenceType.DAY) {
            LOGGER.info("Day recurrence");
            routes.addAll(generateForDayReccurency(route, everyNo, startDate, endDate));
        } else if (recurrenceType == RecurrenceType.WEEK) {
            LOGGER.info("Week recurrence");
           routes.addAll(generateForWeekReccurency(route, days, everyNo));
           LOGGER.info("Routes generated: " + routes);
        }
        return routes;
    }

    @Override
    public void add(RouteEntity route, List<StopEntity> stops, List<Integer> days) throws Exception {
        try {
            UserEntity user = validateUserType();
            stops.forEach(route::addStop);
            route.setCompanyEntity(user.getCompanyEntity());
            List<RouteEntity> routes = generateRoutes(route, days);
            routeRepository.saveAll(routes);
        } catch (Exception ex) {
            throw new Exception("Add failed!" + ex.getMessage());
        }
    }

    @Override
    public Optional<RouteEntity> getById(int id) throws Exception {
        return routeRepository.findById(id);
    }

    @Override
    public void modify(RouteEntity routeEntity, List<StopEntity> stops) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username)
                .get();
        if (userCurrent.getUserType()
                .equals(UserType.CLIENT)) {
            throw new Exception("Not allowed.");
        }

        try {
            routeRepository.save(routeEntity);
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
        validateUserType();
        RouteEntity routeToRemove = routeRepository.findRoute(routeEntity.getStartDateTime(),
                routeEntity.getEndDateTime(), routeEntity.getStartLocation(), routeEntity.getEndLocation());
        if (routeToRemove == null) {
            throw new Exception("Route not found!");
        }
        try {
            if (Boolean.TRUE.equals(removeAll)) {
                if (routeToRemove.getRecurrenceType() != RecurrenceType.NONE) {
                    List<RouteEntity> routesToDelete = getAllRoutesToDelete(routeToRemove);
                    routeRepository.deleteAll(routesToDelete);

                } else {
                    throw new Exception("There is no recurrence for this route");
                }
            } else {
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


    public List<RouteEntity> search(String search, String startDate, String endDate, String startLocation,
                                    String endLocation, String passengersNo) {
        List<RouteEntity> foundRoutes;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateParsed = startDate.equals("null") ? null : LocalDate.parse(startDate, dateTimeFormatter);
        LocalDate endDateParsed = endDate.equals("null") ? null : LocalDate.parse(endDate, dateTimeFormatter);
        int passengers = passengersNo.equals("null") ? 0 : Integer.parseInt(passengersNo);

        Predicate<RouteEntity> filterPredicate = route -> true;
        if (!search.equals("null")) {
            filterPredicate = filterPredicate.and(route -> route.getStartLocation().contains(search) || route.getEndLocation().contains(search));
        }
        if (startDateParsed != null) {
            filterPredicate = filterPredicate.and(route -> route.getStartDateTime().toLocalDate().isEqual(startDateParsed));
        }
        if (endDateParsed != null) {
            filterPredicate = filterPredicate.and(route -> route.getEndDateTime().toLocalDate().isEqual(endDateParsed));
        }
        if (!startLocation.equals("null")) {
            filterPredicate = filterPredicate.and(route -> route.getStartLocation().equals(startLocation));
        }
        if (!endLocation.equals("null")) {
            filterPredicate = filterPredicate.and(route -> route.getEndLocation().equals(endLocation));
        }
        if (passengers > 0) {
            filterPredicate = filterPredicate.and(route -> route.getAvailableSeats() >= passengers);
        }

        // Apply filter predicate
        foundRoutes = routeRepository.findAll().stream()
                .filter(filterPredicate)
                .toList();

        return foundRoutes;
    }

    @Override
    public List<RouteEntity> getRoutesForCompany(String company) throws Exception {
        return routeRepository.findByCompany(company);
    }

}
