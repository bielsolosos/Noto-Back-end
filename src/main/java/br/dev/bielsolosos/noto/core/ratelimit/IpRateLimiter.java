package br.dev.bielsolosos.noto.core.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.dev.bielsolosos.noto.core.ratelimit.enums.IpRateLimitConfigEnum;

/**
 * Annotation to apply rate limiting based on client IP (for anonymous routes)
 * or Username (for authenticated routes) via Resilience4j.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpRateLimiter {
    /**
     * The rate limiter configuration instance to use.
     * Defaults to PUBLIC_ROUTES.
     */
    IpRateLimitConfigEnum value() default IpRateLimitConfigEnum.PUBLIC_ROUTES;
}
