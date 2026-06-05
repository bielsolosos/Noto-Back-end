package br.dev.bielsolosos.noto.core.ratelimit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <b>Papel da Classe:</b>
 * Enumeração strongly-typed que mapeia os identificadores das configurações de rate limit definidos
 * no arquivo de configuração {@code application.yml} da aplicação.
 * <p>
 * <b>Como funciona:</b>
 * Cada constante do enum associa-se ao nome correspondente da configuração do Resilience4j
 * (ex: {@code PUBLIC_ROUTES} mapeia para {@code "public-routes"}). A anotação {@link IpRateLimiter}
 * exige um valor deste enum, removendo o uso de strings soltas no código.
 * <p>
 * <b>Dependências:</b>
 * <ul>
 *   <li>Lombok {@link Getter} e {@link AllArgsConstructor}: Para gerar automaticamente construtores e getters da propriedade {@code name}.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum IpRateLimitConfigEnum {

    PUBLIC_ROUTES("public-routes"),
    PRIVATE_ROUTES("private-routes");

    private String name;
}
