package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.BookingLinkEntity;
import com.licenta.bustravel.model.LinkEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.repositories.BookingLinkRepository;
import com.licenta.bustravel.repositories.BookingRepository;
import com.licenta.bustravel.repositories.LinkRepository;
import com.licenta.bustravel.repositories.RouteRepository;
import com.licenta.bustravel.repositories.StopsRepository;
import com.licenta.bustravel.service.BookingService;
import com.licenta.bustravel.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final RouteRepository routeRepository;
    private final BookingLinkRepository bookingLinkRepository;
    private final RouteServiceImpl routeService;
    private final LinkRepository linkRepository;
    private final StopsRepository stopsRepository;

    @Override
    public Integer add(BookingEntity booking, List<LinkEntity> links) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
            String username = authentication.getName();
            UserEntity user = userService.getByUsername(username);
            booking.setUserEntity(user);
            booking.setTime(LocalDateTime.now());
            BookingEntity savedBooking = bookingRepository.save(booking);
            Set<RouteEntity> uniqueRoutes = links.stream()
                .map(LinkEntity::getRoute)
                .collect(Collectors.toSet());
            uniqueRoutes.forEach(
                route -> route.setAvailableSeats(route.getAvailableSeats() - booking.getPassegersNo()));
            AtomicInteger order = new AtomicInteger(0);
            links.forEach(link -> {
                LinkEntity linkEntity = linkRepository.findByFromStopAndToStopAndRoute(stopsRepository.findStop(
                    link.getFromStop()
                        .getLocation(), link.getFromStop()
                        .getAddress()), stopsRepository.findStop(link.getToStop()
                    .getLocation(), link.getToStop()
                    .getAddress()), link.getRoute());
                bookingLinkRepository.save(BookingLinkEntity.builder()
                    .id(0)
                    .booking(booking)
                    .link(linkEntity)
                    .order(order.getAndIncrement())
                    .startTime(link.getStartTime())
                    .endTime(link.getEndTime())
                    .build());
            });
            return savedBooking.getId();
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
            List<BookingLinkEntity> bookingLinks = bookingLinkRepository.findAllByBookingId(booking.getId());
            Set<RouteEntity> uniqueRoutes = bookingLinks.stream()
                .map(bookingLink -> bookingLink.getLink()
                    .getRoute())
                .collect(Collectors.toSet());
            uniqueRoutes.forEach(
                route -> route.setAvailableSeats(route.getAvailableSeats() - booking.getPassegersNo()));
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
    public List<List<BookingLinkEntity>> getBookingsForUser(String username) throws Exception {
        UserEntity user = userService.getByUsername(username);
        List<BookingEntity> bookings = bookingRepository.findByUserEntity(user);
        return bookings.stream()
            .map(BookingEntity::getBookingLinks)
            .filter(links -> !links.isEmpty())
            .toList();
    }

    @Override
    public List<BookingEntity> getBookingsForRoute(int routeId) throws Exception {
        RouteEntity route = routeRepository.findById(routeId)
            .orElseThrow(() -> new Exception("Route not found!"));
        return bookingLinkRepository.findAll()
            .stream()
            .filter(bookingLink -> bookingLink.getLink()
                .getRoute()
                .getId() == routeId)
            .map(BookingLinkEntity::getBooking)
            .toList();
    }

    @Override
    public List<BookingLinkEntity> getBookingLinksForBooking(int bookingId) {
        List<BookingLinkEntity> bookingLinkEntitie = bookingLinkRepository.findAllByBookingId(bookingId);
        List<BookingLinkEntity> bookingLinkEntities = bookingLinkRepository.findAllByBookingId(bookingId)
            .stream()
            .map(bookingLink -> {
                Map<String, LocalDateTime> timeMap = routeService.getLinksTime(bookingLink.getLink());
                bookingLink.setStartTime(timeMap.get("start"));
                bookingLink.setEndTime(timeMap.get("end"));
                return bookingLink;
            })
            .toList();
        return bookingLinkEntities;
    }

    @Override
    public List<BookingLinkEntity> getBookingsForCompany(String company) {
        List<BookingLinkEntity> bookingLinks = bookingLinkRepository.findAll()
            .stream()
            .filter(bookingLink -> bookingLink.getLink()
                .getRoute()
                .getCompanyEntity()
                .getName()
                .trim()
                .equals(company))
            .toList();
        List<String> names = bookingLinkRepository.findAll()
            .stream()
            .map(bookingLink -> bookingLink.getLink()
                .getRoute()
                .getCompanyEntity()
                .getName())
            .toList();
        return bookingLinks;
    }
}




