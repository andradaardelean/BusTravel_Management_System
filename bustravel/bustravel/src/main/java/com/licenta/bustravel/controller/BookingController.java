package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.BookingDTO;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.enums.BookingType;
import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.RouteService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/booking")
@CrossOrigin
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private RouteService routeService;
    @Autowired
    JwtService jwtService;

    @GetMapping("")
    public @ResponseBody ResponseEntity<?> getAllBookings(@RequestHeader("Authorization") String authorization ) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token already invalidated!");
            }
            return ResponseEntity.ok(bookingService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Get all bookings does not work. " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public @ResponseBody ResponseEntity<List<BookingEntity>> getBookingsByUsername(@RequestHeader String authorization, String username) {
        try{
            String token = authorization.substring(7);
            if(!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            return ResponseEntity.ok(bookingService.getBookingsForUser(username));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> addBooking(@RequestHeader("Authorization") String authorization, @RequestBody BookingDTO booking) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token already invalidated!");
            }
//            bookingService.add(new BookingEntity(0,booking.getPassengersNo(), LocalDateTime.now(), routeService.getById(booking.getRouteId()).get(),
//                    BookingType.valueOf(booking.getBookingType()), booking.getUser()
            return ResponseEntity.ok("Booking added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Add booking does not work. " + e.getMessage());
        }
    }
}
