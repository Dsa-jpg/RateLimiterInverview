package org.nachtman.ratelimiterdemo.ratelimiter;

public interface RateLimiter {

        boolean allowRequest(String clientId);

}
