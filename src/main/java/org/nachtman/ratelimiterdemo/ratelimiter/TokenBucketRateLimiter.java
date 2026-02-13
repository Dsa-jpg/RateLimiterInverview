package org.nachtman.ratelimiterdemo.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketRateLimiter implements RateLimiter {


    private final int maxTokens;

    private final long lastRefill;

    private final ConcurrentHashMap<String,Bucket> clientsMap = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int maxTokens, long lastRefill) {
        this.maxTokens = maxTokens;
        this.lastRefill = lastRefill;
    }


    @Override
    public boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();

        clientsMap.compute(clientId, (id,bucket)->
        {
            if (bucket == null) {
                return new Bucket(new AtomicInteger(maxTokens-1), now);
            }
            long elapsedTime = now - bucket.startTime();
            System.out.println("Elapsed time " + elapsedTime);
            long tokensToAdd = elapsedTime - lastRefill;
            System.out.println("Tokens to add " + tokensToAdd);
            int newTokens = (int) Math.min(bucket.tokens().get() + tokensToAdd, maxTokens);
            System.out.println("Number of new tokens " + newTokens);

            AtomicInteger updateTokens = new AtomicInteger(newTokens);
            updateTokens.decrementAndGet();

            return new Bucket(updateTokens,now);

        });



        return clientsMap.get(clientId).tokens().get() >= 0;
    }
}
