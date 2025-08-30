// BookingService.java
package com.bookify.bookify_app.service;

import com.bookify.bookify_app.model.Booking;
import com.bookify.bookify_app.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository repo;

    public BookingService(BookingRepository repo) {
        this.repo = repo;
    }

    public List<Booking> getAll(String clinicId) {
        return repo.findByClinicId(clinicId);
    }

    public Optional<Booking> getById(String id) {
        return repo.findById(id);
    }

    public Booking save(Booking booking) {
        return repo.save(booking);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
