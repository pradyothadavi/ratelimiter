package com.github.pradyothadavi.core;

import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitManager{

    private Map<String,RateLimitKey> methodToRateLimitKeyMap = new HashMap<String, RateLimitKey>();
    private Map<String,RateLimiter> rateLimiterMap = new HashMap<String, RateLimiter>();

    public Map<String, RateLimiter> getRateLimiterMap() {
        return rateLimiterMap;
    }

    public Map<String, RateLimitKey> getMethodToRateLimitKeyMap() {
        return methodToRateLimitKeyMap;
    }

    public RateLimitKey getRateLimitKey(String methodName){
        return this.methodToRateLimitKeyMap.get(methodName);
    }

    public void setRateLimitKey(String methodName, RateLimitKey rateLimitKey){
        this.methodToRateLimitKeyMap.put(methodName,rateLimitKey);
    }

    public RateLimiter getRateLimiter(String key) {
        return rateLimiterMap.get(key);
    }

    public void setRateLimiter(String key, RateLimiter rateLimiter) {
        this.rateLimiterMap.put(key,rateLimiter);
    }
}
