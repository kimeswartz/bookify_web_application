package com.bookify.bookify_app.filter;

// ********************************************************************************************
// * TenantSubdomainFilter extracts the subdomain from the Host header of incoming requests.  *
// * If a subdomain matches, it resolves the clinic ID via ClinicService and stores it in     *
// * TenantContext for the current request thread.                                            *
// * After the request is processed, the clinic context is cleared to avoid leakage.          *
// * WHY: Enables multi-tenant routing by mapping requests to the correct clinic based on     *
// * their subdomain (e.g., clinic1.bookApp.com -> clinic1’s ID).                             *
// ********************************************************************************************

import com.bookify.bookify_app.service.ClinicService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order(2) // Runs after CorrelationIdFilter (order = 1) but before controller logic
public class TenantSubdomainFilter extends OncePerRequestFilter {

    // Regex to capture the subdomain part of "something.minapp.se"
    private static final Pattern SUB = Pattern.compile("^(?<sub>[^.]+)\\.minapp\\.se(:\\d+)?$");

    private final ClinicService clinicService;

    public TenantSubdomainFilter(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        // Get the full "Host" header (example: hudvardskliniken.minapp.se)
        String host = Optional.ofNullable(req.getHeader("Host")).orElse("");
        Matcher m = SUB.matcher(host);

        if (m.find()) {
            // Extract only the subdomain part
            String sub = m.group("sub");

            // Use the safe lookup (returns null if not found, no exception here)
            // Important: We want the *controller* to decide what happens if a clinic is missing,
            // not the filter. That way we can return a clean 404 JSON error.
            String clinicId = clinicService.resolveClinicBySubDomainOrNull(sub);
            if (clinicId != null) {
                // Store the clinicId for this request thread
                TenantContext.setClinicId(clinicId);
            }
        }

        try {
            // Pass the request down the filter chain
            chain.doFilter(req, res);
        } finally {
            // Always clear the context to prevent "leaking" clinicId across requests
            TenantContext.clear();
        }
    }

}


