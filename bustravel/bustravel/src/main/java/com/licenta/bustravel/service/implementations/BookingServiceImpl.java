package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.BookingEntity;
import com.licenta.bustravel.repositories.BookingRepository;
import com.licenta.bustravel.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Override
    public void add(BookingEntity booking) throws Exception {
        try {
            bookingRepository.save(booking);
            System.out.println("Saved booking: " + booking);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @Override
    public Optional getById(int id) throws Exception {
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
}
