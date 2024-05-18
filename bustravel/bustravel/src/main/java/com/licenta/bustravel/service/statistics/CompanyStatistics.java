package com.licenta.bustravel.service.statistics;

import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.CompanyService;
import com.licenta.bustravel.service.RouteService;
import com.licenta.bustravel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CompanyStatistics {
    private final CompanyService companyService;
    private final RouteService routeService;
    private final BookingService bookingService;
    private final UserService userService;
    public Integer getNumberOfEmployees(String company) throws Exception {
        return userService.getUsersByCompany(company).size();
    }

    public Integer getNumberOfRoutes(String company) throws Exception {
        return routeService.getRoutesForCompany(company).size();
    }

    public Integer getNumberOfBookings(String company) {
        return bookingService.getBookingsForCompany(company).size();
    }

    public Double getKmPerDay(CompanyEntity company, LocalDate date){
        return routeService.getKmPerDay(company, date);
    }

    public Double getKmPerMonth(CompanyEntity company, LocalDate date){
        return routeService.getKmPerMonth(company, date);
    }

    public Double getMoneyPerDay(CompanyEntity company, LocalDate date){
        return routeService.getMoneyPerDay(company, date);
    }

    public Double getMoneyPerMonth(CompanyEntity company, LocalDate date){
        return routeService.getMoneyPerMonth(company, date);
    }

    public Map<String, Double> getStatistics(String name) throws Exception {
        CompanyEntity company = companyService.getByName(name);
        Map<String, Double> statistics = new HashMap<>();
        statistics.put("numberOfEmployees", (double) getNumberOfEmployees(name));
        statistics.put("numberOfRoutes", (double) getNumberOfRoutes(name));
        statistics.put("numberOfBookings", (double) getNumberOfBookings(name));
        statistics.put("kmPerDay", getKmPerDay(company, LocalDate.now()));
        statistics.put("kmPerMonth", getKmPerMonth(company, LocalDate.now()));
        statistics.put("moneyPerDay", getMoneyPerDay(company, LocalDate.now()));
        statistics.put("moneyPerMonth", getMoneyPerMonth(company, LocalDate.now()));
        return statistics;
    }
}
