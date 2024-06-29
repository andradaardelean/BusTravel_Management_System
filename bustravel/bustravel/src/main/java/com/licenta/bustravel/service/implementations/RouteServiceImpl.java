package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.controller.RouteController;
import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.LinkEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.RecurrenceType;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.BookingLinkRepository;
import com.licenta.bustravel.repositories.BookingRepository;
import com.licenta.bustravel.repositories.LinkRepository;
import com.licenta.bustravel.repositories.RouteRepository;
import com.licenta.bustravel.repositories.StopsRepository;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.RouteService;
import com.licenta.bustravel.service.utils.AStar;
import com.licenta.bustravel.service.utils.DistanceMatrix;
import com.licenta.bustravel.service.utils.Graph;
import com.licenta.bustravel.service.utils.Link;
import com.licenta.bustravel.service.utils.Node;
import com.licenta.bustravel.service.utils.DepthFirstSearch;
import com.licenta.bustravel.service.utils.PathSegment;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RouteServiceImpl implements RouteService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class.getName());

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final StopsRepository stopsRepository;
    private final LinkRepository linkRepository;
    private final BookingRepository bookingRepository;
    private final BookingLinkRepository bookingLinkRepository;

    public UserEntity validateUserType() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByOauthId(username)
            .orElseThrow();
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
                            .links(route.getLinks())
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

    public List<RouteEntity> generateForDayReccurency(RouteEntity route, Integer everyNo, LocalDateTime startDate,
                                                      LocalDateTime endDate) {
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
                .links(route.getLinks())
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
        }
        return routes;
    }

    public List<RouteEntity> createLinks(List<RouteEntity> routes, List<StopEntity> stops) {
        routes.forEach(route -> {
            LocalDateTime currentTime = route.getStartDateTime();
            LOGGER.info("Current time: " + currentTime.toString());
            for (int i = 0; i < stops.size() - 1; i++) {
                StopEntity fromStop = stops.get(i);
                fromStop = stopsRepository.findStop(fromStop.getLocation(),
                    fromStop.getAddress()) != null ? stopsRepository.findStop(fromStop.getLocation(),
                    fromStop.getAddress()) : fromStop;
                LOGGER.info("From stop: " + fromStop.toString());
                StopEntity toStop = stops.get(i + 1);
                toStop = stopsRepository.findStop(toStop.getLocation(),
                    toStop.getAddress()) != null ? stopsRepository.findStop(toStop.getLocation(),
                    toStop.getAddress()) : toStop;
                LOGGER.info("To stop: " + toStop.toString());
                Map<String, String> distanceMap = null;
                try {
                    distanceMap = DistanceMatrix.parseData(
                        DistanceMatrix.getData(fromStop.getLocation(), toStop.getLocation()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (distanceMap == null) {
                    LOGGER.error("Error while calculating distance between stops");
                }
                LOGGER.info("Distance map: " + distanceMap.toString());
                Long duration = Long.parseLong(distanceMap.get("durationValue"));
                LocalDateTime nextTime = currentTime.plusSeconds(duration);
                LOGGER.info("Next time: " + nextTime.toString());
                LinkEntity link = LinkEntity.builder()
                    .route(route)
                    .fromStop(fromStop)
                    .toStop(toStop)
                    .distance(Long.parseLong(distanceMap.get("distanceValue")))
                    .distanceText(distanceMap.get("distanceText"))
                    .duration(duration)
                    .durationText(distanceMap.get("durationText"))
                    .price(10.0)
                    .order(i)
                    .startTime(currentTime)
                    .endTime(nextTime)
                    .build();
                LOGGER.info("Link created: " + link.toString());
                currentTime = nextTime;
                if (i == stops.size() - 2) {
                    route.setEndDateTime(currentTime);
                }
                route.getLinks()
                    .add(link);
                fromStop.getFromLinks()
                    .add(link);
                toStop.getToLinks()
                    .add(link);

            }
        });
        return routes;
    }

    @Override
    public void add(RouteEntity route, List<StopEntity> stops, List<Integer> days) throws Exception {
        try {
            UserEntity user = validateUserType();
            route.setCompanyEntity(user.getCompanyEntity());
            List<RouteEntity> routes = generateRoutes(route, days);
            routes = createLinks(routes, stops);
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
        validateUserType();
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
        List<LinkEntity> linksWithBookings = linkRepository.findAllByRoute(routeToRemove).stream()
            .filter(bookingLinkRepository::existsBookingLinkEntityByLink)
            .toList();

        if (!linksWithBookings.isEmpty()) {
            throw new Exception("Route has bookings!");
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


    @Override
    public Map<List<LinkEntity>, String> search(String search, String startDate, String endDate, String startLocation,
                                                String endLocation, String passengersNo) throws Exception {
        try {
            List<RouteEntity> foundRoutes = getFoundRoutes(search, startDate, endDate, passengersNo);
            if (foundRoutes.isEmpty()) {
                throw new Exception("No routes found!");
            }
            List<List<PathSegment>> allPaths = calculatePaths(foundRoutes, startLocation, endLocation, "ALL");
            return getAllPathStops(allPaths);
        }catch(Exception e){
            throw new Exception("Search failed!" + e.getMessage());
        }
    }


    public List<RouteEntity> getFoundRoutes(String search, String startDate, String endDate, String passengersNo){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateParsed = startDate.equals("null") ? null : LocalDate.parse(startDate, dateTimeFormatter);
        LocalDate endDateParsed = endDate.equals("null") ? null : LocalDate.parse(endDate, dateTimeFormatter);
        int passengers = passengersNo.equals("null") ? 0 : Integer.parseInt(passengersNo);

        Predicate<RouteEntity> filterPredicate = route -> true;
        if (startDateParsed != null && endDateParsed != null) {
            filterPredicate = filterPredicate.and(route -> route.getStartDateTime()
                .toLocalDate()
                .plusDays(1)
                .isAfter(startDateParsed));
            filterPredicate = filterPredicate.and(route -> route.getEndDateTime()
                .toLocalDate()
                .minusDays(1)
                .isBefore(endDateParsed));
        } else if (endDateParsed != null || startDateParsed != null) {
            filterPredicate = filterPredicate.and(route -> route.getEndDateTime()
                .toLocalDate()
                .isEqual(startDateParsed != null ? startDateParsed : endDateParsed));
        }
        if (passengers > 0) {
            filterPredicate = filterPredicate.and(route -> route.getAvailableSeats() >= passengers);
        }
        if (!search.equals("null")) {
            filterPredicate = filterPredicate.and(route -> route.getStartLocation()
                .contains(search) || route.getEndLocation()
                .contains(search));
        }

        // Apply filter predicate
        return routeRepository.findAll()
            .stream()
            .filter(filterPredicate)
            .toList();
    }

    public Map<List<LinkEntity>, String> getAllPathStops(List<List<PathSegment>> allPaths) throws Exception {

        Map<List<LinkEntity>, String> allPathsStops = new HashMap<>();
        for (List<PathSegment> path : allPaths) {
            List<LinkEntity> links = new ArrayList<>();
            for (PathSegment pathSegment : path) { // Asigurăm parcurgerea până la penultimul nod pentru a evita
                // erori de index
                Node fromNode = pathSegment.getNode();
                Link link = pathSegment.getLink();
                if (link != null) {
                    Node toNode = link.getDestination();
                    LinkEntity linkEntity = linkRepository.findByFromStopAndToStopAndRoute(fromNode.getStop(),
                        toNode.getStop(), link.getRoute());
                    if (linkEntity != null) {
                        links.add(linkEntity);
                    }
                }
            }

            String distanceText = "";
            String durationText = "";
            if (!links.isEmpty()) {
                StopEntity firstStop = links.get(0)
                    .getFromStop();
                StopEntity lastStop = links.get(links.size() - 1)
                    .getToStop();
                Map<String, String> distanceMap = DistanceMatrix.parseData(
                    DistanceMatrix.getData(firstStop.getLocation(), lastStop.getLocation()));
                distanceText = distanceMap.get("distanceText");
                durationText = distanceMap.get("durationText");
            }

            allPathsStops.put(links, distanceText + " " + durationText);
        }

        return allPathsStops;
    }

    public List<List<PathSegment>> calculatePaths(List<RouteEntity> routes, String startLocation,
                                                     String endLocation, String type) throws Exception {
        Graph graph = buildGraph(routes);
        verifyTransferPoint(graph);
        List<List<PathSegment>> allPaths = new ArrayList<>();

        if (startLocation.equals("null")) {
            startLocation = routes.stream()
                .map(RouteEntity::getStartLocation)
                .findFirst()
                .orElse(null);
        }

        if (endLocation.equals("null")) {
            endLocation = routes.stream()
                .map(RouteEntity::getEndLocation)
                .findFirst()
                .orElse(null);
        }

        StopEntity startLocationEntity = stopsRepository.findStopByLocation(startLocation);
        StopEntity endLocationEntity = stopsRepository.findStopByLocation(endLocation);
        if (startLocationEntity != null && endLocationEntity != null) {
            Node startNode = graph.getNodeByStop(startLocationEntity);
            Node endNode = graph.getNodeByStop(endLocationEntity);
            if(startNode == null){
                throw new Exception("No routes found!");
            }
            if (endNode == null) {
                endNode = new Node(endLocationEntity);
                graph.addNode(endNode);
            }
            startNode.setIsTransferPoint(false);
            if(Objects.equals(type, "ALL"))
                allPaths = DepthFirstSearch.findAllPaths(graph, startNode, endNode);
            else if(Objects.equals(type, "SHORTEST"))
                allPaths = AStar.calculateAllShortestPaths(graph, startNode, endNode);
        }

        return allPaths;
    }

    private Graph buildGraph(List<RouteEntity> routes) throws Exception {
        Graph graph = new Graph();
        for (RouteEntity route : routes) {
            for (LinkEntity link : route.getLinks()) {
                Node fromNode = graph.getNodeByStop(link.getFromStop());
                Node toNode = graph.getNodeByStop(link.getToStop());
                if (fromNode == null) {
                    fromNode = new Node(link.getFromStop());
                    graph.addNode(fromNode);
                }
                if (toNode == null) {
                    toNode = new Node(link.getToStop());
                    graph.addNode(toNode);
                }


                fromNode.addDestination(Link.builder()
                    .destination(toNode)
                    .route(route)
                    .departureTime(link.getStartTime())
                    .arrivalTime(link.getEndTime())
                    .build(), link.getDistance());
            }
        }
        return graph;
    }

    void verifyTransferPoint(Graph graph) {
        for (Node node : graph.getNodes()) {
            Map<LocalDate, Set<Node>> daysWithTransfers = new HashMap<>();

            for (Map.Entry<Link, Long> entry : node.getAdjacentNodes().entrySet()) {
                Link link = entry.getKey();
                Node destination = link.getDestination();
                LocalDate departureDay = link.getDepartureTime().toLocalDate();

                daysWithTransfers.putIfAbsent(departureDay, new HashSet<>());
                daysWithTransfers.get(departureDay).add(destination);
            }

            boolean isTransfer = false;
            for (Set<Node> transfers : daysWithTransfers.values()) {
                if (transfers.size() > 1) {
                    isTransfer = true;
                    break; // Once we know it's a transfer point, no need to check further
                }
            }

            node.setIsTransferPoint(isTransfer); // Set the isTransferPoint property of the node
        }
    }


    @Override
    public List<RouteEntity> getRoutesForCompany(String company) throws Exception {
        return routeRepository.findByCompany(company);
    }

    public Map<String, LocalDateTime> getLinksTime(LinkEntity link) {
        Map<String, LocalDateTime> timeMap = new HashMap<>();
        List<LinkEntity> links = linkRepository.findAllByRouteIdOrderByOrder(link.getRoute()
            .getId());
        LocalDateTime currentTime = link.getRoute()
            .getStartDateTime();
        for (LinkEntity linkEntity : links) {
            if (linkEntity.getFromStop() == link.getFromStop()) {
                timeMap.put("from", currentTime);
            }
            currentTime = currentTime.plusMinutes(linkEntity.getDuration() != null ? linkEntity.getDuration() : 0);
            if (linkEntity.getToStop() == link.getToStop()) {
                timeMap.put("to", currentTime);
                break;
            }
        }
        return timeMap;
    }

    @Override
    public Map<List<LinkEntity>,String> getShortestPath(String search, String startDate, String endDate, String startLocation,
                                            String endLocation, String passengersNo) throws Exception {

        List<RouteEntity> foundRoutes = getFoundRoutes(search, startDate, endDate, passengersNo);
        if (foundRoutes.isEmpty()) {
            return new HashMap<>();
        }
        List<List<PathSegment>> shortestPath = calculatePaths(foundRoutes, startLocation, endLocation, "SHORTEST");
        if (shortestPath.isEmpty()) {
            return new HashMap<>();
        }
        return getAllPathStops(shortestPath);
    }

    public Double getKmPerDay(CompanyEntity companyEntity, LocalDate date) {
        List<RouteEntity> routes = routeRepository.findByCompany(companyEntity.getName());
        double km = 0.0;
        for (RouteEntity route : routes) {
            if(route.getStartDateTime().toLocalDate().isEqual(date)) {
                for (LinkEntity link : route.getLinks()) {
                    km += Double.parseDouble(link.getDistanceText().split(" ")[0]);
                }
            }
        }
        return km;
    }

    public Double getKmPerMonth(CompanyEntity companyEntity, LocalDate date) {
        List<RouteEntity> routes = routeRepository.findByCompany(companyEntity.getName());
        double km = 0.0;
        for (RouteEntity route : routes) {
            if(route.getStartDateTime().getMonth().equals(date.getMonth())) {
                for (LinkEntity link : route.getLinks()) {
                    km += Double.parseDouble(link.getDistanceText().split(" ")[0]);
                }
            }
        }
        return km;
    }

    public Double getMoneyPerDay(CompanyEntity companyEntity, LocalDate date) {
        List<RouteEntity> routes = routeRepository.findByCompany(companyEntity.getName());
        Double money = 0.0;
        for (RouteEntity route : routes) {
            if(route.getStartDateTime().toLocalDate().isEqual(date)) {
                money += route.getPrice();
            }
        }
        return money;
    }

    public Double getMoneyPerMonth(CompanyEntity companyEntity, LocalDate date) {
        List<RouteEntity> routes = routeRepository.findByCompany(companyEntity.getName());
        Double money = 0.0;
        for (RouteEntity route : routes) {
            if(route.getStartDateTime().getMonth().equals(date.getMonth())) {
                money += route.getPrice();
            }
        }
        return money;
    }

}
