package com.licenta.bustravel.service;

import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.BookingLinkEntity;
import com.licenta.bustravel.model.LinkEntity;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    List<BookingLinkEntity> add(BookingEntity booking, List<LinkEntity> links) throws Exception;
    Optional<BookingEntity> getById(int id) throws Exception;
    void modify(BookingEntity booking) throws Exception;
    void delete(BookingEntity booking) throws Exception;
    List<BookingEntity> getAll();
    List<BookingEntity> getBookingsForUser(String username) throws Exception;

    List<BookingEntity> getBookingsForRoute(int routeId) throws Exception;
    List<BookingLinkEntity> getBookingLinksForBooking(int bookingId) throws Exception;

}
