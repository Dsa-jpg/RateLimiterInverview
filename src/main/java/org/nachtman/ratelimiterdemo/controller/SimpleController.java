package org.nachtman.ratelimiterdemo.controller;


import org.nachtman.ratelimiterdemo.ratelimit.FixedWindowRateLimiter;
import org.nachtman.ratelimiterdemo.ratelimit.TokenBucketRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "v1")
public class SimpleController {

    private final FixedWindowRateLimiter fixedWindowRateLimiter;
    private final TokenBucketRateLimiter tokenBucketRateLimiter;
    Logger logger = LoggerFactory.getLogger(SimpleController.class);

    public SimpleController(FixedWindowRateLimiter fixedWindowRateLimiter,
                            TokenBucketRateLimiter tokenBucketRateLimiter) {
        this.fixedWindowRateLimiter = fixedWindowRateLimiter;
        this.tokenBucketRateLimiter = tokenBucketRateLimiter;
    }

    @GetMapping("/test")
    public ResponseEntity<String> getTestDataFixedWindow(@RequestParam("clientId") String clientId) {
        if (!fixedWindowRateLimiter.allowRequestBasic(clientId)) {
            logger.warn("Rate limit exceeded (FixedWindow) for clientId={}", clientId);
            return new ResponseEntity<>("You have sent too many requests.", HttpStatus.TOO_MANY_REQUESTS);
        }
        logger.info("Request accepted (FixedWindow) for clientId={}", clientId);
        return new ResponseEntity<>("Test data", HttpStatus.OK);
    }

    @GetMapping("/test2")
    public ResponseEntity<String> getTestDataTokenBucket(@RequestParam("clientId") String clientId) {
        if (!tokenBucketRateLimiter.allowRequestBasic(clientId)) {
            logger.warn("Rate limit exceeded (TokenBucket) for clientId={}", clientId);
            return new ResponseEntity<>("You have sent too many requests.", HttpStatus.TOO_MANY_REQUESTS);
        }
        logger.info("Request accepted for (TokenBucket) clientId={}", clientId);
        return new ResponseEntity<>("Test data", HttpStatus.OK);
    }
}
