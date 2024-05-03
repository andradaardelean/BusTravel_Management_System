package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.RequestEntity;
import com.licenta.bustravel.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Integer> {
    List<RequestEntity> findAllByStatus(RequestStatus status);
}
