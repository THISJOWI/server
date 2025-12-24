package com.thisjowi.auth.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Order(1)
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    
    // Store buckets per IP address
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // Rate limiting configurations
    private static final int LOGIN_REQUESTS_PER_MINUTE = 5;
    private static final int REGISTER_REQUESTS_PER_MINUTE = 3;
    private static final int CHANGE_PASSWORD_REQUESTS_PER_MINUTE = 3;
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 100;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
            throws ServletException, IOException {
        
        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();
        
        // Get or create bucket for this IP
        Bucket bucket = buckets.computeIfAbsent(clientIp, ip -> createBucketForEndpoint(requestUri));
        
        // Check if request can be allowed
        if (bucket.tryConsume(1)) {
            // Request is allowed
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            logger.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, requestUri);
            response.setStatus(429); // HTTP 429 Too Many Requests
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Please try again later.\"}");
            response.setContentType("application/json");
        }
    }
    
    private Bucket createBucketForEndpoint(String requestUri) {
        Bandwidth limit;
        
        if (requestUri.contains("/auth/login")) {
            limit = Bandwidth.classic(LOGIN_REQUESTS_PER_MINUTE, Refill.intervally(LOGIN_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
        } else if (requestUri.contains("/auth/register")) {
            limit = Bandwidth.classic(REGISTER_REQUESTS_PER_MINUTE, Refill.intervally(REGISTER_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
        } else if (requestUri.contains("/auth/change-password")) {
            limit = Bandwidth.classic(CHANGE_PASSWORD_REQUESTS_PER_MINUTE, Refill.intervally(CHANGE_PASSWORD_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
        } else {
            limit = Bandwidth.classic(DEFAULT_REQUESTS_PER_MINUTE, Refill.intervally(DEFAULT_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
        }
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    private String getClientIp(HttpServletRequest request) {
        // Check for X-Forwarded-For header (proxy)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        // Check for other proxy headers
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        // Fall back to remote address
        return request.getRemoteAddr();
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Don't apply rate limiting to health checks, metrics, etc.
        return path.equals("/actuator/health") || 
               path.equals("/swagger-ui.html") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs");
    }
}
