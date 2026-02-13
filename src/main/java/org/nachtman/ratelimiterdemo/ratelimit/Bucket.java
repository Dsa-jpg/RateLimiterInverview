package org.nachtman.ratelimiterdemo.ratelimit;

import java.util.concurrent.atomic.AtomicInteger;

public record Bucket(AtomicInteger tokens, long startTime) {
}
