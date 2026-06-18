package com.medikids.medikids.utils.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.medikids.medikids.utils.helpers.IpUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final Map<String, ConcurrentLinkedDeque<Long>> attempts = new ConcurrentHashMap<>();

    private static final long LOGIN_WINDOW_MS = 60_000;
    private static final int LOGIN_MAX_ATTEMPTS = 5;

    private static final long REGISTER_WINDOW_MS = 600_000;
    private static final int REGISTER_MAX_ATTEMPTS = 3;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.contains("/auth/") && !path.contains("/usuario/save") && !path.contains("/cliente/save");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request == null || response == null || filterChain == null) {
            return;
        }

        String clientIp = IpUtils.getClientIp(request);
        String path = request.getRequestURI();
        long now = System.currentTimeMillis();

        if (path.contains("/auth/")) {
            if (isRateLimited(clientIp, "login", LOGIN_WINDOW_MS, LOGIN_MAX_ATTEMPTS, now)) {
                log.warn("Rate limit excedido para IP: {} en {}", clientIp, path);
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Demasiadas solicitudes. Intenta nuevamente en 1 minuto.\"}");
                return;
            }
        }

        if (path.contains("/usuario/save") || path.contains("/cliente/save")) {
            if (isRateLimited(clientIp, "register", REGISTER_WINDOW_MS, REGISTER_MAX_ATTEMPTS, now)) {
                log.warn("Rate limit de registro excedido para IP: {} en {}", clientIp, path);
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Demasiados registros desde esta IP. Intenta más tarde.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String key, String prefix, long windowMs, int maxAttempts, long now) {
        String cacheKey = prefix + ":" + key;
        ConcurrentLinkedDeque<Long> timestamps = attempts.computeIfAbsent(cacheKey, k -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            timestamps.removeIf(t -> now - t > windowMs);

            if (timestamps.size() >= maxAttempts) {
                return true;
            }

            timestamps.addLast(now);
            return false;
        }
    }

}
