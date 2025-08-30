package com.bookify.bookify_app.service;

import com.bookify.bookify_app.model.TreatmentCategory;
import com.bookify.bookify_app.repository.TreatmentCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreatmentCategoryService {
    private final TreatmentCategoryRepository repo;

    public TreatmentCategoryService(TreatmentCategoryRepository repo) {
        this.repo = repo;
    }

    public List<TreatmentCategory> getAll(String clinicId) {
        return repo.findByClinicId(clinicId);
    }

    public Optional<TreatmentCategory> getById(String id) {
        return repo.findById(id);
    }

    public TreatmentCategory save(TreatmentCategory category) {
        return repo.save(category);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
