package org.nachtman.ratelimiterdemo.controller;


import org.nachtman.ratelimiterdemo.ratelimiter.FixedWindowRateLimiter;
import org.nachtman.ratelimiterdemo.ratelimiter.TokenBucketRateLimiter;
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


    public SimpleController() {
        this.tokenBucketRateLimiter = new TokenBucketRateLimiter(5, 2_000);;
        this.fixedWindowRateLimiter = new FixedWindowRateLimiter(5, 10_000);
    }

    @GetMapping("/test")
    public ResponseEntity<String> getTestDataFixedWindow(@RequestParam("clientId") String clientId){
        if (!fixedWindowRateLimiter.allowRequest(clientId)){
            return new ResponseEntity<>("You have sent too many requests.", HttpStatus.TOO_MANY_REQUESTS);
        }
        return new ResponseEntity<>("Test data",HttpStatus.OK);
    }

    @GetMapping("/test2")
    public ResponseEntity<String> getTestDataTokenBucket(@RequestParam("clientId") String clientId){
        if (!tokenBucketRateLimiter.allowRequest(clientId)){
            return new ResponseEntity<>("You have sent too many requests.", HttpStatus.TOO_MANY_REQUESTS);
        }
        return new ResponseEntity<>("Test data",HttpStatus.OK);
    }
}
