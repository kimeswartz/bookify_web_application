package com.bookify.bookify_app.service;

// ********************************************************************************************
// * ClinicService provides business logic for resolving clinics by their subdomain.          *
// * It queries the ClinicRepository for a match and returns the clinic ID if found.          *
// * If no clinic exists for the given subdomain, a custom ClinicNotFoundException is thrown. *
// * WHY: Centralizes clinic lookup logic, ensures consistent error handling, and separates   *
// * persistence from application logic.                                                      *
// ********************************************************************************************

import com.bookify.bookify_app.model.Clinic;
import com.bookify.bookify_app.repository.ClinicRepository;
import org.springframework.stereotype.Service;

@Service
public class ClinicService {

    private final ClinicRepository repo;

    public ClinicService(ClinicRepository repo) {
        this.repo = repo;
    }

    /**
     * Not Active ATM
     * Resolve a clinic by subdomain.
     * - Returns clinicId if found.
     * - Throws ClinicNotFoundException if no clinic matches.
     *
     * Use this version if you want a "fail-fast" approach.
     * Example: direct lookups where missing clinic = real error.
     */
    public String resolveClinicBySubDomain(String subDomain) {
        return repo.findBySubdomain(subDomain)
                .map(Clinic::getId)
                .orElseThrow(() -> new ClinicNotFoundException(subDomain));
    }

    /**
     * Safe version of resolveClinicBySubDomain.
     * - Returns clinicId if found.
     * - Returns null if no clinic matches (no exception).
     *
     * Use this in the filter so the filter itself does not
     * break the request lifecycle with exceptions.
     */
    public String resolveClinicBySubDomainOrNull(String subdomain) {
        return repo.findBySubdomain(subdomain)
                .map(Clinic::getId)
                .orElse(null);
    }

    /**
     * Custom exception for missing clinics.
     * It carries the failing subdomain for clearer error messages.
     */
    public static class ClinicNotFoundException extends RuntimeException {
        public ClinicNotFoundException(String subDomain) {
            super("Clinic with domain " + subDomain + " not found");
        }
    }

}
