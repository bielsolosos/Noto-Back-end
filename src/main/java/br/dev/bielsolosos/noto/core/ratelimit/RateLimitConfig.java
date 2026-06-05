package br.dev.bielsolosos.noto.core.ratelimit;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <b>Papel da Classe:</b>
 * Classe de configuração do Spring Web MVC responsável por registrar os interceptadores customizados
 * no ciclo de vida de tratamento de requisições da aplicação.
 * <p>
 * <b>Como funciona:</b>
 * Implementa a interface {@link WebMvcConfigurer} e sobrescreve o método {@code addInterceptors} 
 * para adicionar o {@link IpRateLimitInterceptor} ao {@link InterceptorRegistry}. Isso garante que
 * todas as requisições HTTP mapeadas pelo Spring MVC passem pelo filtro de controle de taxa.
 * <p>
 * <b>Dependências:</b>
 * <ul>
 *   <li>{@link IpRateLimitInterceptor}: O interceptor que de fato implementa a lógica do controle de taxa.</li>
 * </ul>
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    private final IpRateLimitInterceptor ipRateLimitInterceptor;

    public RateLimitConfig(IpRateLimitInterceptor ipRateLimitInterceptor) {
        this.ipRateLimitInterceptor = ipRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipRateLimitInterceptor);
    }
}
