package com.licenta.bustravel.service;

import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.model.RouteEntity;

import java.util.List;
import java.util.Optional;

public interface RouteService {
    void add(List<RouteEntity> routeEntity, List<StopEntity> intermediateLocations) throws Exception;
    Optional getById(int id) throws Exception;
    void modify(RouteEntity routeEntity, List<StopEntity> intermediateLocations) throws Exception;
    void delete(RouteEntity routeEntity, Boolean removeAll) throws Exception;
    List<RouteEntity> getAll() throws Exception;
    List<RouteEntity> search(String startDate, String endDate, String startLocation, String endLocation, String passangersNo) throws Exception;
}
