package com.bookify.bookify_app.service;

import com.bookify.bookify_app.model.ClinicSettings;
import com.bookify.bookify_app.repository.ClinicSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClinicSettingsService {
    private final ClinicSettingsRepository repo;

    public ClinicSettingsService(ClinicSettingsRepository repo) {
        this.repo = repo;
    }

    public Optional<ClinicSettings> getByClinicId(String clinicId) {
        return Optional.ofNullable(repo.findByClinicId(clinicId));
    }

    public ClinicSettings save(ClinicSettings settings) {
        return repo.save(settings);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
