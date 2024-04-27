package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Integer> {
    LinkEntity findByFromStopIdAndToStopId(int fromStopId, int toStopId);
    List<LinkEntity> findAllByRouteIdOrderByOrder(int routeId);
}
