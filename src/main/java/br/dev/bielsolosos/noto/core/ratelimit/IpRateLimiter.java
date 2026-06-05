package br.dev.bielsolosos.noto.core.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.dev.bielsolosos.noto.core.ratelimit.enums.IpRateLimitConfigEnum;

/**
 * <b>Papel da Classe:</b>
 * Anotação de marcação para habilitar o controle de taxa (Rate Limiting) de forma dinâmica
 * a nível de classe (Controller) ou de método (Endpoint).
 * <p>
 * <b>Como funciona:</b>
 * Quando aplicada, o {@link IpRateLimitInterceptor} intercepta as requisições destinadas ao endpoint
 * anotado, lê a configuração definida no atributo {@code value()} e aplica a lógica de limitação.
 * Se o limite for excedido, uma exceção {@code RequestNotPermitted} é lançada e capturada globalmente.
 * <p>
 * <b>Dependências:</b>
 * <ul>
 *   <li>{@link IpRateLimitConfigEnum}: Enumeração das configurações válidas (ex: PUBLIC_ROUTES, PRIVATE_ROUTES).</li>
 *   <li>{@link IpRateLimitInterceptor}: Interceptor do Spring MVC que interpreta esta anotação.</li>
 * </ul>
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
