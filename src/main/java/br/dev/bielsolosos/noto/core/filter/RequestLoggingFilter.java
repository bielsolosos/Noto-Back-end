package br.dev.bielsolosos.noto.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * <b>Papel da Classe:</b>
 * Filtro de Servlets responsável por auditar logs detalhados de todas as requisições HTTP
 * de entrada e suas respectivas respostas HTTP de saída.
 * <p>
 * <b>Como funciona:</b>
 * Intercepta chamadas web na camada do Servlet Container:
 * 1. Envolve a requisição e a resposta em wrappers que cacheiam o corpo do payload.
 * 2. Loga o método HTTP, a URI (com query string), o IP de origem e o User-Agent.
 * 3. Repassa o processamento e, no retorno, calcula a duração total em ms e o status HTTP.
 * 4. Executa o descarregamento do cache de resposta para o stream do cliente.
 * Ativado apenas sob o perfil de produção via {@code @Profile("prod")}.
 * <p>
 * <b>Dependências:</b>
 * <ul>
 *   <li>Spring Web (Filtros e Caching de Stream).</li>
 *   <li>Lombok {@code @Slf4j}.</li>
 * </ul>
 */
@Slf4j
@Component
@Profile("prod")
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        // Wrapper para ler o response depois sem consumir o Stream de forma irreversível
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Informações da requisição de entrada
        String clientIp = getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String userAgent = request.getHeader("User-Agent");

        log.info("REQUISIÇÃO: {} {} | IP: {} | UserAgent: {}",
                method,
                queryString != null ? uri + "?" + queryString : uri,
                clientIp,
                userAgent != null ? userAgent.substring(0, Math.min(50, userAgent.length())) : "N/A");

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();

            log.info("RESPOSTA Status: {} {} | Duração: {}ms | URI: {}",
                    status,
                    getStatusDescription(status),
                    duration,
                    uri);

            // Importante: Copia o buffer acumulado no wrapper de volta para o canal real da resposta
            responseWrapper.copyBodyToResponse();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getStatusDescription(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 301 -> "Moved Permanently";
            case 302 -> "Found";
            case 304 -> "Not Modified";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 429 -> "Too Many Requests";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Evita logging de arquivos estáticos, assets, rotas do Actuator e rotas do Swagger
        return path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator");
    }
}
