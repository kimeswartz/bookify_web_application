package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.Clinic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClinicRepository extends MongoRepository<Clinic, String> {
    Optional<Clinic> findBySubdomain(String subdomain);
}
