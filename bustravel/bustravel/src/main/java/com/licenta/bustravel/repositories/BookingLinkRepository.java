package com.licenta.bustravel.repositories;

import com.licenta.bustravel.model.BookingLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingLinkRepository extends JpaRepository<BookingLinkEntity, Integer>{
    List<BookingLinkEntity> findAllByBookingId(int bookingId);
}
