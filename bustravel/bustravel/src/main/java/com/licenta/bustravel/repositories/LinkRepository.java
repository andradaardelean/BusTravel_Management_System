package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.LinkEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.StopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Integer> {
    LinkEntity findByFromStopAndToStopAndRoute(StopEntity fromStop, StopEntity toStop, RouteEntity route);
    List<LinkEntity> findAllByRouteIdOrderByOrder(int routeId);
    List<LinkEntity> findAllByRoute(RouteEntity route);
}
