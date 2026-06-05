package br.dev.bielsolosos.noto.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * <b>Papel da Classe:</b>
 * Classe de configuração do OpenAPI / Swagger responsável por definir metadados
 * e
 * esquemas de autenticação para a documentação interativa das APIs.
 * <p>
 * <b>Como funciona:</b>
 * Configura o {@link OpenAPI} definindo informações do projeto, servidores
 * locais
 * e o esquema de segurança JWT Bearer. É ativada condicionalmente pela
 * propriedade
 * {@code noto.swagger-enabled=true} (ativa somente no perfil de
 * desenvolvimento).
 * <p>
 * <b>Dependências:</b>
 * <ul>
 * <li>Spring Boot Auto-Configuration.</li>
 * <li>Biblioteca Springdoc OpenAPI.</li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

        @Bean
        @ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Noto API")
                                                .version("2.0.0")
                                                .description("API para gerenciamento de notas e conteúdos")
                                                .contact(new Contact()
                                                                .name("Gabriel Coutinho")
                                                                .url("https://github.com/bielsolosos"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .addServersItem(new Server()
                                                .url("http://localhost:8080")
                                                .description("Servidor de Desenvolvimento Local"))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer Authentication",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Insira o token JWT no formato: Bearer {token}")));
        }
}
