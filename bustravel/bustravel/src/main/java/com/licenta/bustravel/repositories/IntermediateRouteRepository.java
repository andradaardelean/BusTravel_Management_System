package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.IntermediateRoutesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntermediateRouteRepository extends JpaRepository<IntermediateRoutesEntity, Integer> {
}
