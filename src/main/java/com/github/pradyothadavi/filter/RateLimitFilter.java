package com.github.pradyothadavi.filter;

import com.github.pradyothadavi.core.RateLimitKey;
import com.github.pradyothadavi.core.RateLimitManager;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    private int TOO_MANY_REQUESTS = 429;
    private ResourceInfo resourceInfo;
    private RateLimitManager rateLimitManager;

    public RateLimitFilter(ResourceInfo resourceInfo, RateLimitManager rateLimitManager) {
        this.resourceInfo = resourceInfo;
        this.rateLimitManager = rateLimitManager;
    }

    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        RateLimitKey rateLimitKey = rateLimitManager.getRateLimitKey(resourceInfo.getResourceMethod().getName());
        String rateLimiterKey = rateLimitKey.computeKey(resourceInfo,containerRequestContext);
        RateLimiter rateLimiter = rateLimitManager.getRateLimiter(rateLimiterKey);
        if(null != rateLimiter){
            if(!rateLimiter.tryAcquire()){
                Exception cause = new IllegalAccessException("Too many requests trying to access "+resourceInfo.getResourceMethod().getName());
                throw new WebApplicationException(cause, Response.status(TOO_MANY_REQUESTS).build());
            }
        }
    }
}
