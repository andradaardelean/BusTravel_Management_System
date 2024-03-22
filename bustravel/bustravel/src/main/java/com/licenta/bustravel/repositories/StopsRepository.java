package com.licenta.bustravel.repositories;

import com.licenta.bustravel.entities.CompanyEntity;
import com.licenta.bustravel.entities.RouteEntity;
import com.licenta.bustravel.entities.StopEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StopsRepository extends JpaRepository<StopEntity, Integer> {
    @Query("select stop from StopEntity stop where stop.location=:location and stop.order=:order and stop.stop=:stop and stop.stopOrder=:stopOrder")
    StopEntity findStop(String  location, Integer order, String stop, Integer stopOrder);
}
