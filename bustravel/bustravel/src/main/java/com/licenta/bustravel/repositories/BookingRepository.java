package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
}
