package com.bookify.bookify_app.filter;

// ********************************************************************************************
// * TenantSubdomainFilter is responsible for resolving the tenant (clinic) context based on  *
// * the subdomain of incoming requests.                                                      *
// *                                                                                          *
// * HOW IT WORKS:                                                                            *
// * - Extracts the Host header (or X-Forwarded-Host when behind proxies).                    *
// * - Matches subdomains using the regex: "<sub>.minapp.se[:port]"                           *
// * - If a valid subdomain is found, it attempts to resolve a clinicId via ClinicService.    *
// * - If a match is found, the clinicId is stored in TenantContext for the current request.  *
// * - After the request finishes, TenantContext is cleared to prevent cross-request leaks.   *
// *                                                                                          *
// * WHY: This enables multi-tenancy by routing requests to the correct clinic based on       *
// * their subdomain (e.g., clinic1.minapp.se -> clinic1’s ID).                               *
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
@Order(2) // Runs after CorrelationIdFilter (1), before controller logic
public class TenantSubdomainFilter extends OncePerRequestFilter {

    // Regex pattern that extracts the subdomain from "<sub>.minapp.se" (optionally with a port, e.g., ":443")
    private static final Pattern SUB = Pattern.compile("^(?<sub>[^.]+)\\.minapp\\.se(:\\d+)?$");

    private final ClinicService clinicService;

    public TenantSubdomainFilter(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        // Use forwarded host if available (proxy/load balancer), otherwise fall back to Host header
        String hostHeader = Optional.ofNullable(req.getHeader("X-Forwarded-Host"))
                .orElseGet(() -> Optional.ofNullable(req.getHeader("Host")).orElse(""))
                .toLowerCase();

        Matcher m = SUB.matcher(hostHeader);

        if (m.find()) {
            String sub = m.group("sub");

            // Resolve clinicId based on subdomain (non-throwing; only set if found)
            clinicService.resolveClinicIdBySubdomainOptional(sub)
                    .ifPresent(TenantContext::setClinicId);
        } else {
            // Optional extension point: support localhost or other dev/test domains
            // Example: if (hostHeader.startsWith("localhost")) { ... }}
        }

        try {
            chain.doFilter(req, res);
        } finally {
            // Always clear tenant context to avoid leaking state across requests
            TenantContext.clear();
        }
    }
}


