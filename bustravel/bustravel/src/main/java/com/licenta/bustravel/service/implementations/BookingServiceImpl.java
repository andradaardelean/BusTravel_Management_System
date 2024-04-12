package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.repositories.BookingRepository;
import com.licenta.bustravel.repositories.RouteRepository;
import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RouteRepository routeRepository;
    @Override
    public void add(BookingEntity booking) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserEntity user = userService.getByUsername(username);
            booking.setUserEntity(user);
            bookingRepository.save(booking);
            System.out.println("Saved booking: " + booking);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @Override
    public Optional<BookingEntity> getById(int id) throws Exception {
        return bookingRepository.findById(id);
    }

    @Override
    public void modify(BookingEntity booking) throws Exception {
        try {
            bookingRepository.save(booking);
            System.out.println("Modified booking: " + booking);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @Override
    public void delete(BookingEntity booking) throws Exception {
        try {
            bookingRepository.delete(booking);
            System.out.println("Deleted booking: " + booking);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @Override
    public List<BookingEntity> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public List<BookingEntity> getBookingsForUser(String username) throws Exception {
        System.out.println(username);
        UserEntity user = userService.getByUsername(username);
        System.out.println("Found user: " + user);
        List<BookingEntity> bookings = bookingRepository.findByUserEntity(user);
        System.out.println("Found bookings: " + bookings);
        return bookings;
    }

    @Override
    public List<BookingEntity> getBookingsForRoute(int routeId) throws Exception {
        RouteEntity route = routeRepository.findById(routeId).orElseThrow(() -> new Exception("Route not found!"));
        return bookingRepository.findByRouteEntity(route);
    }
}
