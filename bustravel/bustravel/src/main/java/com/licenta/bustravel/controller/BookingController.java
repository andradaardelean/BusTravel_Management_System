package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.AddBookingDTO;
import com.licenta.bustravel.DTO.BookingLinkDTO;
import com.licenta.bustravel.DTO.mapper.BookingMapper;
import com.licenta.bustravel.DTO.mapper.LinkMapper;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.BookingLinkEntity;
import com.licenta.bustravel.model.LinkEntity;
import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping("api/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {
    private final BookingService bookingService;

    private final JwtService jwtService;

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
            return new ResponseEntity<>(BookingMapper.toDTOList(bookingService.getBookingsForUser(username)),
                HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }
    }

    @PostMapping
    public @ResponseBody ResponseEntity<?> addBooking(@RequestHeader("Authorization") String authorization,
                                                      @RequestBody AddBookingDTO addBookingDTO) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token already invalidated!");
            }

            BookingEntity bookingEntity = BookingMapper.toModel(addBookingDTO.getBooking());
            List<LinkEntity> links = addBookingDTO.getLinks()
                .stream()
                .map(LinkMapper::mapToModel)
                .toList();
            List<BookingLinkDTO> bookingLinkDTOs = bookingService.add(bookingEntity, links)
                .stream()
                .map(BookingMapper::toBookingLinkDTO)
                .toList();
            return new ResponseEntity<>(bookingLinkDTOs, HttpStatus.OK);
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
            return new ResponseEntity<>(BookingMapper.toDTOList(bookingService.getBookingsForRoute(routeid)),
                HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<?> getLinksByBookingId(@RequestHeader("Authorization") String authorization,
                                                               @PathVariable int id) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
            List<BookingLinkDTO> links = bookingService.getBookingLinksForBooking(id)
                .stream()
                .map(BookingMapper::toBookingLinkDTO)
                .toList();
            return new ResponseEntity<>(links, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        }
    }

    @DeleteMapping("/{id}")
    public @ResponseBody ResponseEntity<?> deleteBooking(@RequestHeader("Authorization") String authorization,
                                                         @PathVariable int id) {
        try {
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token already invalidated!");
            BookingEntity booking = bookingService.getById(id)
                .orElseThrow(() -> new Exception("Booking not found!"));
            bookingService.delete(booking);
            return ResponseEntity.ok("Booking deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Delete booking does not work. " + e.getMessage());
        }
    }

    //adu booking uri pt companie
}
