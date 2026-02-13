package org.nachtman.ratelimiterdemo.ratelimit;

public interface RateLimiter {

        boolean allowRequestBasic(String clientId);
}
