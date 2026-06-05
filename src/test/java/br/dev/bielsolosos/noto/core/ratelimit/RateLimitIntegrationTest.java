package br.dev.bielsolosos.noto.core.ratelimit;

import br.dev.bielsolosos.noto.domain.users.model.dto.TokenResponse;
import br.dev.bielsolosos.noto.domain.users.service.AuthService;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Clear rate limiter instances for test isolation
        rateLimiterRegistry.remove("public-routes-127.0.0.1");
        rateLimiterRegistry.remove("public-routes-192.168.1.1");
        rateLimiterRegistry.remove("public-routes-192.168.1.2");

        // Mock AuthService behavior
        TokenResponse tokenResponse = new TokenResponse("mock-access-token", "mock-refresh-token");
        when(authService.login(any())).thenReturn(tokenResponse);
    }

    @Test
    void shouldAllowRequestsWithinLimitAndBlockExceeding() throws Exception {
        // We make 30 calls successfully (limit is 30)
        for (int i = 0; i < 30; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .remoteAddress("127.0.0.1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"test\",\"password\":\"test\"}"))
                    .andExpect(status().isOk());
        }

        // The 31st call should trigger 429 Too Many Requests
        mockMvc.perform(post("/api/auth/login")
                        .remoteAddress("127.0.0.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"test\"}"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void shouldIsolateLimitsByIpAddress() throws Exception {
        // IP 192.168.1.1 exhausts its rate limit
        for (int i = 0; i < 30; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .remoteAddress("192.168.1.1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"test\",\"password\":\"test\"}"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/api/auth/login")
                        .remoteAddress("192.168.1.1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"test\"}"))
                .andExpect(status().isTooManyRequests());

        // IP 192.168.1.2 has a separate bucket and should succeed
        mockMvc.perform(post("/api/auth/login")
                        .remoteAddress("192.168.1.2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"test\"}"))
                .andExpect(status().isOk());
    }
}
