package com.bookify.bookify_app.controller;

// ********************************************************************************************
// * ClinicController exposes an admin API endpoint for retrieving the current clinic ID.     *
// * It reads the clinic identifier from TenantContext and returns it as JSON.                *
// * WHY: Provides clients (e.g., admin tools) with visibility into which clinic (tenant)     *
// * the current request is associated with, based on subdomain resolution.                   *
// ********************************************************************************************

import com.bookify.bookify_app.filter.TenantContext;
import com.bookify.bookify_app.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/clinic")
public class ClinicController {

    /**
     * Returns the clinic ID resolved for the current request.
     *
     * Flow:
     *  1. TenantSubdomainFilter already extracted the subdomain from Host header.
     *  2. If a matching clinic was found, its ID is stored in TenantContext.
     *  3. We read the clinicId here and return it as JSON.
     *  4. If no clinicId is present, we throw ClinicNotFoundException,
     *     which is translated into a 404 JSON error by GlobalExceptionHandler.
     *
     * Example success response:
     *   { "clinicId": "64f2..." }
     *
     * Example error response (unknown clinic):
     *   {
     *     "type": "https://bookify.dev/errors/clinic-not-found",
     *     "title": "Clinic Not Found",
     *     "status": 404,
     *     "instance": "/api/v1/admin/clinic",
     *     "correlationID": "..."
     *   }
     */

    @GetMapping
    public ResponseEntity<Map<String, Object>> currentClinic() {
        String clinicId = TenantContext.getClinicId();
        if (clinicId == null) {
            // Important: throwing here instead of filter ensures we can return
            // a clean, consistent ProblemDetail JSON via GlobalExceptionHandler.
            throw new ClinicService.ClinicNotFoundException("Unknown clinic for request");
        }
        // Return clinicId in a simple JSON map
        return ResponseEntity.ok(Map.of("clinicId", clinicId));
    }

}
