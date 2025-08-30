package com.bookify.bookify_app.service;

import com.bookify.bookify_app.model.StaffMember;
import com.bookify.bookify_app.repository.StaffMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffMemberService {
    private final StaffMemberRepository repo;

    public StaffMemberService(StaffMemberRepository repo) {
        this.repo = repo;
    }

    public List<StaffMember> getAll(String clinicId) {
        return repo.findByClinicId(clinicId);
    }

    public Optional<StaffMember> getById(String id) {
        return repo.findById(id);
    }

    public StaffMember save(StaffMember staff) {
        return repo.save(staff);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}

