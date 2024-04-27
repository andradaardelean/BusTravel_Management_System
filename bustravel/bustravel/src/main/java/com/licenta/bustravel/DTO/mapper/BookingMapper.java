package com.licenta.bustravel.DTO.mapper;

import com.licenta.bustravel.DTO.BookingDTO;
import com.licenta.bustravel.DTO.BookingLinkDTO;
import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.model.BookingLinkEntity;
import com.licenta.bustravel.model.RouteEntity;
import com.licenta.bustravel.model.enums.BookingType;
import java.util.List;

public class BookingMapper {
    public static BookingDTO toDTO(BookingEntity booking) {
        return BookingDTO.builder()
            .id(booking.getId())
            .passengersNo(booking.getPassegersNo())
            .routeId(booking.getRouteEntity()
                .getId())
            .type(booking.getType().toString())
            .build();
    }


    public static BookingEntity toModel(BookingDTO booking) {
        return BookingEntity.builder()
            .id(booking.getId())
            .passegersNo(booking.getPassengersNo())
            .routeEntity(RouteEntity.builder()
                .id(booking.getRouteId())
                .build())
            .type(BookingType.valueOf(booking.getType()))
            .build();
    }

    public static List<BookingDTO> toDTOList(List<BookingEntity> bookings) {
        return bookings.stream()
            .map(BookingMapper::toDTO)
            .toList();
    }

    public static BookingLinkDTO toBookingLinkDTO(BookingLinkEntity bookingLink) {
        return BookingLinkDTO.builder()
            .id(bookingLink.getId())
            .booking(BookingMapper.toDTO(bookingLink.getBooking()))
            .fromStop(StopMapper.toDTO(bookingLink.getLink().getFromStop()))
            .toStop(StopMapper.toDTO(bookingLink.getLink().getToStop()))
            .distanceText(bookingLink.getLink()
                .getDistanceText())
            .durationText(bookingLink.getLink()
                .getDurationText())
            .price(bookingLink.getLink()
                .getPrice())
            .order(bookingLink.getOrder())
            .startTime(bookingLink.getStartTime().toString())
            .endTime(bookingLink.getEndTime().toString())
            .build();
    }

}
