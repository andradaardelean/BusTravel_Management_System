package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.BookingDTO;
import com.licenta.bustravel.DTO.mapper.BookingMapper;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("api/booking")
@CrossOrigin
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private RouteService routeService;
    @Autowired
    JwtService jwtService;

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @GetMapping()
    public @ResponseBody ResponseEntity<?> getAllBookings(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token already invalidated!");
            }
            return new ResponseEntity<>(BookingMapper.toDTOList(bookingService.getAll()), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Get all bookings does not work. " + e.getMessage());
        }
    }

    @GetMapping("/byUsername/{username}")
    public @ResponseBody ResponseEntity<?> getBookingsByUsername(@RequestHeader String authorization,
                                                                                   @PathVariable String username) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            return new ResponseEntity<>(BookingMapper.toDTOList(bookingService.getBookingsForUser(username)), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> addBooking(@RequestHeader("Authorization") String authorization,
                                                      @RequestBody BookingDTO booking) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token already invalidated!");
            }

            RouteEntity route = routeService.getById(booking.getRouteId())
                    .orElseThrow(() -> new Exception("Route not found!"));

            BookingEntity bookingEntity = BookingMapper.toModel(booking);
            bookingEntity.setRouteEntity(route);
            bookingService.add(bookingEntity);
            return ResponseEntity.ok("Booking added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Add booking does not work. " + e.getMessage());
        }
    }

    @GetMapping("/byRoute/{routeid}")
    public @ResponseBody ResponseEntity<?> getBookingsForRoute(@RequestHeader("Authorization") String authorization,
                                                                                   @PathVariable int routeid) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            return new ResponseEntity<>(BookingMapper.toDTOList(bookingService.getBookingsForRoute(routeid)), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
}
