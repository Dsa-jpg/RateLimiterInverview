package org.nachtman.ratelimiterdemo.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketRateLimiterTest {

    private FixedWindowRateLimiter limiter;

    @BeforeEach
    void setUp() {
        limiter = new FixedWindowRateLimiter(3, 10_000);
    }


    @Test
    void shouldHandleDifferentClientsSeparately() {
        String clientA = "clientA";
        String clientB = "clientB";

        assertTrue(limiter.allowRequestBasic(clientA));
        assertTrue(limiter.allowRequestBasic(clientA));
        assertTrue(limiter.allowRequestBasic(clientA));
        assertFalse(limiter.allowRequestBasic(clientA));

        assertTrue(limiter.allowRequestBasic(clientB));
    }

    @Test
    void shouldAllowExactlyLimitRequests() {
        String clientA = "clientA";

        assertTrue(limiter.allowRequestBasic(clientA));
        assertTrue(limiter.allowRequestBasic(clientA));
        assertTrue(limiter.allowRequestBasic(clientA));
        assertFalse(limiter.allowRequestBasic(clientA));
    }

}