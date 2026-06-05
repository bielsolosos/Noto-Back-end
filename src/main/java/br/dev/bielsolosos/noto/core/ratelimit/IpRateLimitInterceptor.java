package br.dev.bielsolosos.noto.core.ratelimit;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class IpRateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterRegistry rateLimiterRegistry;

    public IpRateLimitInterceptor(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        IpRateLimiter annotation = handlerMethod.getMethodAnnotation(IpRateLimiter.class);
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(IpRateLimiter.class);
        }

        if (annotation != null) {
            String configName = annotation.value().getName();
            String clientKey = resolveClientKey(request);
            String rateLimiterName = configName + "-" + clientKey;

            log.debug("Applying rate limit '{}' for key '{}'", configName, clientKey);

            RateLimiter limiter = rateLimiterRegistry.rateLimiter(rateLimiterName, configName);
            if (!limiter.acquirePermission()) {
                log.warn("Rate limit exceeded for key '{}' on URI '{}'", clientKey, request.getRequestURI());
                throw RequestNotPermitted.createRequestNotPermitted(limiter);
            }
        }

        return true;
    }

    private String resolveClientKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }

        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty() && !"unknown".equalsIgnoreCase(xfHeader)) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
