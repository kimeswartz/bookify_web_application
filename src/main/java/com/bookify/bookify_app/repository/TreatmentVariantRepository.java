package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.TreatmentVariant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TreatmentVariantRepository extends MongoRepository<TreatmentVariant, String> {
    List<TreatmentVariant> findByClinicId(String clinicId);
}
