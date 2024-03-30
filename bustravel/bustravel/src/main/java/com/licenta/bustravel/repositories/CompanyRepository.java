package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {
    Optional<CompanyEntity> findByName(String name);
    @Query("select company from CompanyEntity company where company.name=:name")
    CompanyEntity getByName(String name);
}
