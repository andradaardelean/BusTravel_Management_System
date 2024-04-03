package com.licenta.bustravel.service;

import com.licenta.bustravel.model.BookingEntity;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    void add(BookingEntity booking) throws Exception;
    Optional getById(int id) throws Exception;
    void modify(BookingEntity booking) throws Exception;
    void delete(BookingEntity booking) throws Exception;
    List<BookingEntity> getAll();
    List<BookingEntity> getBookingsForUser(String username) throws Exception;

}
