package com.bookify.bookify_app.service;

import com.bookify.bookify_app.model.Treatment;
import com.bookify.bookify_app.repository.TreatmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreatmentService {
    private final TreatmentRepository repo;

    public TreatmentService(TreatmentRepository repo) {
        this.repo = repo;
    }

    public List<Treatment> getAll(String clinicId) {
        return repo.findByClinicId(clinicId);
    }

    public Optional<Treatment> getById(String id) {
        return repo.findById(id);
    }

    public Treatment save(Treatment treatment) {
        return repo.save(treatment);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}

