package org.nachtman.ratelimiterdemo.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;

public record RequestCounter(AtomicInteger count, long startTime) { }
