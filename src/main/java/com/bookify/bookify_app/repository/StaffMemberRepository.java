package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.StaffMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface StaffMemberRepository extends MongoRepository<StaffMember, String> {
    List<StaffMember> findByClinicId(String clinicId);
    Optional<StaffMember> findByEmailAndClinicId(String email, String clinicId);
}
