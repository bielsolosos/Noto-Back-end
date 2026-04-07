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
@ConfigurationProperties(prefix = "r2")
public class R2Properties {

    private String endpoint = "";
    private String accessKey = "";
    private String secretKey = "";
    private String bucketName = "";
    private String publicUrlBase = "";
}

