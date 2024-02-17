package com.licenta.bustravel.repositories;

import com.licenta.bustravel.entities.CompanyEntity;
import com.licenta.bustravel.entities.StopEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopsRepository extends JpaRepository<StopEntity, Integer> {
}
