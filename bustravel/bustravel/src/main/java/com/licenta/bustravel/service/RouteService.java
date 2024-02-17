package com.licenta.bustravel.service;

import com.licenta.bustravel.entities.StopEntity;
import com.licenta.bustravel.entities.RouteEntity;

import java.util.List;
import java.util.Optional;

public interface RouteService {
    void add(RouteEntity routeEntity, List<StopEntity> intermediateLocations) throws Exception;
    Optional getById(int id) throws Exception;
    void modify(RouteEntity routeEntity, List<StopEntity> intermediateLocations) throws Exception;
    void delete(RouteEntity routeEntity) throws Exception;
    List<RouteEntity> getAll() throws Exception;
}
