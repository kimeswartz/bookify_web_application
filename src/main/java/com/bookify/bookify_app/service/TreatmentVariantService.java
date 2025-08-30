package com.bookify.bookify_app.service;

import com.bookify.bookify_app.model.TreatmentVariant;
import com.bookify.bookify_app.repository.TreatmentVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreatmentVariantService {
    private final TreatmentVariantRepository repo;

    public TreatmentVariantService(TreatmentVariantRepository repo) {
        this.repo = repo;
    }

    public List<TreatmentVariant> getAll(String clinicId) {
        return repo.findByClinicId(clinicId);
    }

    public Optional<TreatmentVariant> getById(String id) {
        return repo.findById(id);
    }

    public TreatmentVariant save(TreatmentVariant variant) {
        return repo.save(variant);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}

