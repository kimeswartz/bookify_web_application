package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.Treatment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TreatmentRepository extends MongoRepository<Treatment, String> {
    List<Treatment> findByClinicId(String clinicId);
}
