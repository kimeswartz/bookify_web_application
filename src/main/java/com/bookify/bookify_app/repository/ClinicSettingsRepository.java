package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.ClinicSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ClinicSettingsRepository extends MongoRepository<ClinicSettings, String> {
    ClinicSettings findByClinicId(String clinicId);
}
