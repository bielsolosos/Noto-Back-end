package br.dev.bielsolosos.noto.core.ratelimit;

import br.dev.bielsolosos.noto.core.ratelimit.enums.IpRateLimitConfigEnum;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpRateLimitInterceptorTest {

    @Mock
    private RateLimiterRegistry rateLimiterRegistry;

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private IpRateLimitInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new IpRateLimitInterceptor(rateLimiterRegistry);
        SecurityContextHolder.setContext(securityContext);
    }

    // Helper classes for testing annotations on method and class level
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    static class TestController {
        @IpRateLimiter(IpRateLimitConfigEnum.PUBLIC_ROUTES)
        public void annotatedMethod() {}

        public void unannotatedMethod() {}
    }

    @Test
    void shouldPassIfNoAnnotationIsPresent() throws Exception {
        // Arrange
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(IpRateLimiter.class)).thenReturn(null);
        // Cast bean type to a class that does NOT have the annotation
        when(handlerMethod.getBeanType()).thenAnswer(inv -> Object.class);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
        verifyNoInteractions(rateLimiterRegistry);
    }

    @Test
    void shouldApplyRateLimitByIpIfAnonymousUsingRemoteAddr() throws Exception {
        // Arrange
        Method method = TestController.class.getMethod("annotatedMethod");
        IpRateLimiter annotation = method.getAnnotation(IpRateLimiter.class);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(IpRateLimiter.class)).thenReturn(annotation);

        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        when(rateLimiterRegistry.rateLimiter("public-routes-192.168.0.1", "public-routes")).thenReturn(rateLimiter);
        when(rateLimiter.acquirePermission()).thenReturn(true);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
        verify(rateLimiterRegistry).rateLimiter("public-routes-192.168.0.1", "public-routes");
    }

    @Test
    void shouldApplyRateLimitByIpIfAnonymousUsingXForwardedFor() throws Exception {
        // Arrange
        Method method = TestController.class.getMethod("annotatedMethod");
        IpRateLimiter annotation = method.getAnnotation(IpRateLimiter.class);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(IpRateLimiter.class)).thenReturn(annotation);

        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 70.41.3.18");

        when(rateLimiterRegistry.rateLimiter("public-routes-203.0.113.195", "public-routes")).thenReturn(rateLimiter);
        when(rateLimiter.acquirePermission()).thenReturn(true);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
        verify(rateLimiterRegistry).rateLimiter("public-routes-203.0.113.195", "public-routes");
    }

    @Test
    void shouldApplyRateLimitByUsernameIfAuthenticated() throws Exception {
        // Arrange
        Method method = TestController.class.getMethod("annotatedMethod");
        IpRateLimiter annotation = method.getAnnotation(IpRateLimiter.class);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(IpRateLimiter.class)).thenReturn(annotation);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("userPrincipal");
        when(authentication.getName()).thenReturn("bielsolosos");

        when(rateLimiterRegistry.rateLimiter("public-routes-bielsolosos", "public-routes")).thenReturn(rateLimiter);
        when(rateLimiter.acquirePermission()).thenReturn(true);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
        verify(rateLimiterRegistry).rateLimiter("public-routes-bielsolosos", "public-routes");
    }

    @Test
    void shouldThrowRequestNotPermittedWhenRateLimitExceeded() throws Exception {
        // Arrange
        Method method = TestController.class.getMethod("annotatedMethod");
        IpRateLimiter annotation = method.getAnnotation(IpRateLimiter.class);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(IpRateLimiter.class)).thenReturn(annotation);

        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        when(rateLimiterRegistry.rateLimiter("public-routes-192.168.0.1", "public-routes")).thenReturn(rateLimiter);
        when(rateLimiter.acquirePermission()).thenReturn(false);
        when(rateLimiter.getName()).thenReturn("public-routes-192.168.0.1");
        when(rateLimiter.getRateLimiterConfig()).thenReturn(io.github.resilience4j.ratelimiter.RateLimiterConfig.ofDefaults());

        // Act & Assert
        assertThrows(RequestNotPermitted.class, () -> {
            interceptor.preHandle(request, response, handlerMethod);
        });
    }

    @Test
    void shouldFallbackToClassLevelAnnotationIfMethodNotAnnotated() throws Exception {
        // Arrange
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(IpRateLimiter.class)).thenReturn(null);
        // Cast bean type to a class that DOES have the annotation
        when(handlerMethod.getBeanType()).thenAnswer(inv -> TestController.class);

        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        when(rateLimiterRegistry.rateLimiter("private-routes-192.168.0.1", "private-routes")).thenReturn(rateLimiter);
        when(rateLimiter.acquirePermission()).thenReturn(true);

        // Act
        boolean result = interceptor.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
        verify(rateLimiterRegistry).rateLimiter("private-routes-192.168.0.1", "private-routes");
    }
}
