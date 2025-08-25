package com.bookify.bookify_app.filter;

// ********************************************************************************************
// * RateLimitFilter applies simple request rate limiting to sensitive endpoints.             *
// * It restricts clients to a maximum number of requests per time window (sliding window).   *
// * If the limit is exceeded, a 429 Too Many Requests response is returned.                  *
// * Runs first in the filter chain to block early before processing continues.               *
// ********************************************************************************************

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(0) // Executes before other filters (e.g. CorrelationIdFilter @Order(1))
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMIT = 20;              // max requests per window
    private static final long WINDOW_MS = 60_000L;    // 1 minute
    private static final Set<String> GUARDED = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/public/bookings"
    );

    private final Map<String, Deque<Long>> limits = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest req,
            @NonNull HttpServletResponse res,
            @NonNull FilterChain fc
    ) throws ServletException, IOException {

        String path = req.getRequestURI();
        if (GUARDED.contains(path)) {
            String key = Optional.ofNullable(req.getHeader("X-Forwarded-For"))
                    .orElse(req.getRemoteAddr());

            long now = System.currentTimeMillis();
            Deque<Long> q = limits.computeIfAbsent(key, k -> new ArrayDeque<>());

            synchronized (q) {
                // Remove timestamps outside of the current time window
                while (!q.isEmpty() && (now - q.peekFirst()) > WINDOW_MS) {
                    q.pollFirst();
                }

                // If request count exceeds the limit, block and return 429
                if (q.size() >= LIMIT) {
                    res.setStatus(429); // Too Many Requests
                    res.setHeader("Retry-After", String.valueOf(WINDOW_MS / 1000));
                    return;
                }
                // Record current request timestamp
                q.addLast(now);
            }
        }

        fc.doFilter(req, res);
    }
}
