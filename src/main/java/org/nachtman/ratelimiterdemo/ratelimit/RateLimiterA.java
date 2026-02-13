package org.nachtman.ratelimiterdemo.ratelimit;

public interface RateLimiterA {
    RateLimitResult allowRequest(String clientId);
}
