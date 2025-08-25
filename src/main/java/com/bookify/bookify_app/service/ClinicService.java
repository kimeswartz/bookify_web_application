package com.bookify.bookify_app.service;

// ********************************************************************************************
// * ClinicService contains business logic for resolving clinics by their subdomain.          *
// *                                                                                          *
// * It exposes two variants for lookup:                                                      *
// *   - Optional variant: returns Optional<String>, avoids exceptions, useful in filters.    *
// *   - Throwing variant: fails fast with ClinicNotFoundException, for stricter contexts.    *
// *                                                                                          *
// * WHY: Makes the API explicit and safe for both filters and controllers depending on use   *
// * case (graceful handling vs. strict validation).                                          *
// ********************************************************************************************

import com.bookify.bookify_app.model.Clinic;
import com.bookify.bookify_app.repository.ClinicRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClinicService {

    private final ClinicRepository repo;

    public ClinicService(ClinicRepository repo) {
        this.repo = repo;
    }


    /**
     * Attempt to resolve a clinicId from a given subdomain.
     * Returns an Optional, which will be empty if no clinic is found.
     * Useful in filters where absence of a clinic should not immediately fail.
     *
     * @param subdomain the subdomain that identifies the clinic
     * @return Optional containing clinicId if found, otherwise Optional.empty()
     */

    public Optional<String> resolveClinicIdBySubdomainOptional(String subdomain) {
        return repo.findBySubdomain(subdomain).map(Clinic::getId);
    }

    /**
     * Resolve a clinicId from a given subdomain, or throw an exception if none is found.
     * Use this in controllers or business logic where missing clinics should be treated as errors.
     *
     * @param subdomain the subdomain that identifies the clinic
     * @return the resolved clinicId
     * @throws ClinicNotFoundException if no clinic matches the given subdomain
     */

    public String resolveClinicIdBySubdomain(String subdomain) {
        return resolveClinicIdBySubdomainOptional(subdomain)
                .orElseThrow(() -> new ClinicNotFoundException(subdomain));
    }

    /**
     * Custom runtime exception thrown when no clinic is found for a given subdomain.
     */

    public static class ClinicNotFoundException extends RuntimeException {
        public ClinicNotFoundException(String subdomain) {
            super("Clinic with domain " + subdomain + " not found");
        }
    }
}
