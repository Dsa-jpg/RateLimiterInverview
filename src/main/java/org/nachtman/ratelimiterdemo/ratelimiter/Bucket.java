package org.nachtman.ratelimiterdemo.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;

public record Bucket(AtomicInteger tokens, long startTime) {
}
