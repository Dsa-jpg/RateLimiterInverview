package org.nachtman.ratelimiterdemo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nachtman.ratelimiterdemo.ratelimit.FixedWindowRateLimiter;
import org.nachtman.ratelimiterdemo.ratelimit.TokenBucketRateLimiter;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleControllerTest {

    private FixedWindowRateLimiter fixedLimiter;
    private TokenBucketRateLimiter tokenLimiter;
    private SimpleController controller;

    @BeforeEach
    void setUp() {
        fixedLimiter = mock(FixedWindowRateLimiter.class);
        tokenLimiter = mock(TokenBucketRateLimiter.class);

        controller = new SimpleController(fixedLimiter, tokenLimiter);
    }

    @Test
    void shouldReturnOkWhenAllowed() {
        when(fixedLimiter.allowRequestBasic("client1"))
                .thenReturn(true);

        var response = controller.getTestDataFixedWindow("client1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test data", response.getBody());
    }

    @Test
    void shouldReturn429WhenBlocked() {
        when(fixedLimiter.allowRequestBasic("client1"))
                .thenReturn(false);

        var response = controller.getTestDataFixedWindow("client1");

        assertEquals(HttpStatus.TOO_MANY_REQUESTS,
                response.getStatusCode());
    }
}