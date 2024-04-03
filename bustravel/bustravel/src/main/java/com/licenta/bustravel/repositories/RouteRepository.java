package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Integer>, JpaSpecificationExecutor<RouteEntity> {
    @Query("select route from RouteEntity route where route.startDateTime=:startDateTime and route.endDateTime=:endDateTime and route.startLocation=:startLocation and route.endLocation=:endLocation")
    RouteEntity findRoute(LocalDateTime startDateTime, LocalDateTime endDateTime, String startLocation, String endLocation);


    @Query("SELECT route FROM RouteEntity route WHERE :startDateTime IS NULL OR route.startDateTime = :startDateTime " +
            "AND :endDateTime IS NULL OR route.endDateTime = :endDateTime " +
            "AND :startLocation IS NULL OR route.startLocation = :startLocation " +
            "AND :endLocation IS NULL OR route.endLocation = :endLocation")
    List<RouteEntity> searchRoute(LocalDateTime startDateTime, LocalDateTime endDateTime, String startLocation, String endLocation);
}
