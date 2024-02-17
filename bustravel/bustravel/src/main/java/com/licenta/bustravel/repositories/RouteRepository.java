package com.licenta.bustravel.repositories;

import com.licenta.bustravel.entities.CompanyEntity;
import com.licenta.bustravel.entities.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Integer> {

}
