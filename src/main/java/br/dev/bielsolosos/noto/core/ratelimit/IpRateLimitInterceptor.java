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

/**
 * <b>Papel da Classe:</b>
 * Interceptor do Spring MVC que intercepta as requisições HTTP destinadas a controllers
 * ou métodos anotados com {@link IpRateLimiter} para aplicar o controle de taxa.
 * <p>
 * <b>Como funciona:</b>
 * No método {@code preHandle}:
 * 1. Verifica se a rota possui a anotação {@link IpRateLimiter}.
 * 2. Se possuir, identifica o cliente gerando uma chave única:
 *    - Usuários autenticados: usa o {@code username} obtido do {@link SecurityContextHolder}.
 *    - Usuários anônimos: usa o endereço IP (lendo o cabeçalho {@code X-Forwarded-For} para proxies ou caindo para {@code request.getRemoteAddr()}).
 * 3. Recupera ou cria dinamicamente uma instância de {@code RateLimiter} no {@code RateLimiterRegistry}
 *    usando a chave gerada e aplicando as configurações especificadas no enum.
 * 4. Consome uma permissão. Caso falhe (limite excedido), lança {@code RequestNotPermitted}, 
 *    que é capturado pelo Exception Handler global retornando HTTP 429.
 * <p>
 * <b>Dependências:</b>
 * <ul>
 *   <li>{@link RateLimiterRegistry} (Resilience4j): Registro para gerenciar as instâncias dinâmicas de limitadores.</li>
 *   <li>{@link SecurityContextHolder} (Spring Security): Para identificar usuários logados e isolar limites por conta.</li>
 *   <li>{@link IpRateLimiter}: A anotação que marca as regras de rate limit.</li>
 * </ul>
 */
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
