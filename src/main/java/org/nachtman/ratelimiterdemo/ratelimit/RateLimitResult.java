package org.nachtman.ratelimiterdemo.ratelimit;

public record RateLimitResult(
        boolean allowed,
        int requestLimit,
        int remainingPermits,
        long timeRemaining
) {}
