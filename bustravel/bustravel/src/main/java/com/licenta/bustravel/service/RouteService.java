package com.licenta.bustravel.service;

import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.LinkEntity;
import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.model.RouteEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RouteService {
    void add(RouteEntity routeEntity, List<StopEntity> intermediateLocations, List<Integer> days, String date) throws Exception;
    Optional<RouteEntity> getById(int id) throws Exception;
    void modify(RouteEntity routeEntity, List<StopEntity> intermediateLocations) throws Exception;
    void delete(RouteEntity routeEntity, Boolean removeAll) throws Exception;
    List<RouteEntity> getAll() throws Exception;
    Map<List<LinkEntity>, String> search(String search, String startDate, String endDate, String startLocation, String endLocation, String passangersNo) throws Exception;
    List<RouteEntity> getRoutesForCompany(String company) throws Exception;
    Map<List<LinkEntity>,String> getShortestPath(String search, String startDate, String endDate, String startLocation,
                                                 String endLocation, String passengersNo) throws Exception;
    Double getKmPerDay(CompanyEntity companyEntity, LocalDate date);
    Double getKmPerMonth(CompanyEntity companyEntity, LocalDate date);
    Double getMoneyPerDay(CompanyEntity companyEntity, LocalDate date);
    Double getMoneyPerMonth(CompanyEntity companyEntity, LocalDate date);
}
