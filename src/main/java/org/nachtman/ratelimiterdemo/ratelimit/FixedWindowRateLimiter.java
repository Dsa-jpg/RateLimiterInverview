package org.nachtman.ratelimiterdemo.ratelimit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter implements RateLimiter, RateLimiterA {


    private final int maxRequests;

    private final long windowSize;

    private final ConcurrentHashMap<String, RequestCounter> clientMap = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }


    @Override
    public boolean allowRequestBasic(String clientId) {
        long now = System.currentTimeMillis();

        RequestCounter requestCounter = getRequestCounter(clientId, now);

        return requestCounter.count().get() <= maxRequests;
    }

    @Override
    public RateLimitResult allowRequest(String clientId) {
        long now = System.currentTimeMillis();

        RequestCounter requestCounter = getRequestCounter(clientId, now);

        return new RateLimitResult(
                requestCounter.count().get() <= maxRequests,
                requestCounter.count().get(),
                Math.max(0, maxRequests - requestCounter.count().get()),
                (requestCounter.startTime() + windowSize) - now
        );
    }

    private RequestCounter getRequestCounter(String clientId, long now) {
        return clientMap.compute(clientId, (id, counter) -> {
            if (counter == null || now - counter.startTime() >= windowSize) {
                return new RequestCounter(new AtomicInteger(1), now);
            } else {
                counter.count().incrementAndGet();
                return counter;
            }

        });
    }
}
