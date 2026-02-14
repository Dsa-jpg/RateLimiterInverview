package org.nachtman.ratelimiterdemo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nachtman.ratelimiterdemo.ratelimit.FixedWindowRateLimiter;
import org.nachtman.ratelimiterdemo.ratelimit.TokenBucketRateLimiter;
import org.springframework.http.HttpStatus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    void shouldNotExceedLimitUnderConcurrency() throws InterruptedException {
        int limit = 10;
        fixedLimiter = new FixedWindowRateLimiter(limit, 60_000);
        controller = new SimpleController(fixedLimiter, tokenLimiter);

        int threads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    var response = controller.getTestDataFixedWindow("client1");
                    if (response.getStatusCode() == HttpStatus.OK) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(limit, successCount.get(), "Exceeded allowed request limit!");
    }
}