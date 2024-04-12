package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
    List<BookingEntity> findByUserEntity(UserEntity userEntity);

    List<BookingEntity> findByRouteEntity(RouteEntity routeEntity);
}
