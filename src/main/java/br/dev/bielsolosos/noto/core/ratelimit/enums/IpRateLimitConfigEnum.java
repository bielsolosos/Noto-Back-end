package br.dev.bielsolosos.noto.core.ratelimit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IpRateLimitConfigEnum {

    PUBLIC_ROUTES("public-routes"),
    PRIVATE_ROUTES("private-routes");

    private String name;
}
