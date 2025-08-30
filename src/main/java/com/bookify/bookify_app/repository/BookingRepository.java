package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByClinicId(String clinicId);
}