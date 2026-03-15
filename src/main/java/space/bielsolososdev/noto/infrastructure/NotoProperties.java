package space.bielsolososdev.noto.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "noto")
public class NotoProperties {

    private Jwt jwt = new Jwt();
    private boolean registrationEnabled;

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "";
        private long expiration = 3600000L;
        private long refreshExpiration = 86400000L;
    }
}

