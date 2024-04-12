package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.BookingDTO;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.enums.BookingType;
import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.RouteService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
//            List<String> roles = jwtService.extractClaim(token, claims -> claims.get("roles", List.class));
//            System.out.println("Roles: " + roles);
            return ResponseEntity.ok(bookingService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Get all bookings does not work. " + e.getMessage());
        }
    }

    @GetMapping("/byUsername/{username}")
    public @ResponseBody ResponseEntity<?> getBookingsByUsername(@RequestHeader String authorization,
                                                                                   @PathVariable String username) {
        try {
            LOGGER.info("Getting bookings for user: " + username);
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            List<BookingEntity> bookings = bookingService.getBookingsForUser(username);
//            return ResponseEntity.ok(bookingService.getBookingsForUser(username));
//            return ResponseEntity.ok(bookingService.getBookingsForUser(username));

            return new ResponseEntity<>(bookings, HttpStatus.OK);
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

            bookingService.add(new BookingEntity(0, booking.getPassengersNo(), LocalDateTime.now(), route, null,
                    BookingType.valueOf(booking.getType())));
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
            LOGGER.info("Getting bookings for route: " + routeid);
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            List<BookingEntity> bookings = bookingService.getBookingsForRoute(routeid);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
}
