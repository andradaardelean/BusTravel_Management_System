package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);
    @Query("select user from UserEntity user where user.companyEntity.name=:company")
    List<UserEntity> findByCompany(String company);
}
