package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.StopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StopsRepository extends JpaRepository<StopEntity, Integer> {
    @Query("select stop from StopEntity stop where stop.location=:location and stop.address=:address")
    StopEntity findStop(String  location, String address);

    StopEntity findStopByLocation(String location);
}
