package org.nachtman.ratelimiterdemo.ratelimit;

import java.util.concurrent.atomic.AtomicInteger;

public record RequestCounter(AtomicInteger count, long startTime) { }
