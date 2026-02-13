package org.nachtman.ratelimiterdemo.ratelimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter implements RateLimiter{


    private final int maxRequests;

    private final long windowSize;

    private final ConcurrentHashMap<String,RequestCounter> clientMap = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }


    @Override
    public boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();

        clientMap.compute(clientId, (id,counter) -> {
           if (counter==null || now - counter.startTime() >= windowSize ){
               return new RequestCounter(new AtomicInteger(1),now);
           }
           else {
               counter.count().incrementAndGet();
               return counter;
           }

        });


        return clientMap.get(clientId).count().get() <= maxRequests;
    }
}
