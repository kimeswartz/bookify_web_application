package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUserId(String userId); // To delete old tokens
}
