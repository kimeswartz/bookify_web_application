package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.TreatmentCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TreatmentCategoryRepository extends MongoRepository<TreatmentCategory, String> {
    List<TreatmentCategory> findByClinicId(String clinicId);
}
