package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Integer> {
}
