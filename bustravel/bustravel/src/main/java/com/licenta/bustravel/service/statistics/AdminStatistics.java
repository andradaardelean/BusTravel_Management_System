package com.licenta.bustravel.service.statistics;

import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.CompanyService;
import com.licenta.bustravel.service.RequestService;
import com.licenta.bustravel.service.RouteService;
import com.licenta.bustravel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@Service
public class AdminStatistics {
    private final CompanyService companyService;
    private final UserService userService;
    private final RouteService routeService;
    private final BookingService bookingService;
    private final RequestService requestService;
    public Integer getNumberOfUsers() throws Exception {
        return userService.getAll().size();
    }

    public Integer getNumberOfCompanies() throws Exception {
        return companyService.getAll().size();
    }

    public Integer getNumberOfRoutes() throws Exception {
        return routeService.getAll().size();
    }

    public Integer getNumberOfBookings(){
        return bookingService.getAll().size();
    }

    public Integer getNumberOfRequests(){
        return requestService.getAllRequests("ALL").size();
    }

    public Map<String, Integer> getStatistics() throws Exception {
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("users", getNumberOfUsers());
        statistics.put("companies", getNumberOfCompanies());
        statistics.put("routes", getNumberOfRoutes());
        statistics.put("bookings", getNumberOfBookings());
        statistics.put("requests", getNumberOfRequests());
        return statistics;
    }
}
