package org.nachtman.ratelimiterdemo.controller;

import org.nachtman.ratelimiterdemo.ratelimit.FixedWindowRateLimiter;
import org.nachtman.ratelimiterdemo.ratelimit.RateLimitResult;
import org.nachtman.ratelimiterdemo.ratelimit.TokenBucketRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "v2")
public class AdvanceController {
    private final FixedWindowRateLimiter fixedWindowRateLimiter;
    private final TokenBucketRateLimiter tokenBucketRateLimiter;


    public AdvanceController() {
        this.tokenBucketRateLimiter = new TokenBucketRateLimiter(5, 2_000);
        this.fixedWindowRateLimiter = new FixedWindowRateLimiter(5, 10_000);
    }

    @GetMapping("/test")
    public ResponseEntity<String> getTestDataFixedWindow(@RequestParam("clientId") String clientId) {
        RateLimitResult rateLimitResult = fixedWindowRateLimiter.allowRequest(clientId);
        if (!rateLimitResult.allowed()) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-RateLimit-Limit", String.valueOf(rateLimitResult.requestLimit()))
                    .header("X-RateLimit-Remaining", String.valueOf(Math.max(0, rateLimitResult.remainingPermits())))
                    .header("Retry-After", String.valueOf(rateLimitResult.timeRemaining() / 1000))
                    .body("You have sent too many requests.");
        }
        return ResponseEntity
                .ok()
                .header("X-RateLimit-Limit", String.valueOf(rateLimitResult.requestLimit()))
                .header("X-RateLimit-Remaining", String.valueOf(Math.max(0, rateLimitResult.remainingPermits())))
                .body("Test data");
    }

    @GetMapping("/test2")
    public ResponseEntity<String> getTestDataTokenBucket(@RequestParam("clientId") String clientId) {
        if (!tokenBucketRateLimiter.allowRequestBasic(clientId)) {
            return new ResponseEntity<>("You have sent too many requests.", HttpStatus.TOO_MANY_REQUESTS);
        }
        return new ResponseEntity<>("Test data", HttpStatus.OK);
    }
}
