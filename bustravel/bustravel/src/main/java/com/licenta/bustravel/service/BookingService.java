package com.licenta.bustravel.service;

import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.BookingLinkEntity;
import com.licenta.bustravel.model.LinkEntity;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Integer add(BookingEntity booking, List<LinkEntity> links) throws Exception;
    Optional<BookingEntity> getById(int id) throws Exception;
    void modify(BookingEntity booking) throws Exception;
    void delete(BookingEntity booking) throws Exception;
    List<BookingEntity> getAll();
    List<List<BookingLinkEntity>> getBookingsForUser(String username) throws Exception;

    List<BookingLinkEntity> getBookingsForRoute(int routeId) throws Exception;
    List<BookingLinkEntity> getBookingLinksForBooking(int bookingId) throws Exception;

    List<BookingLinkEntity> getBookingsForCompany(String company);
}
