package com.github.pradyothadavi.filter;

import com.github.pradyothadavi.core.RateLimitAttribute;
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

      if(null != resourceInfo){
        RateLimitKey rateLimitKey = rateLimitManager.getRateLimitKey(resourceInfo.getResourceMethod().getName());
        if(null != rateLimitKey){
          String rateLimiterKey = rateLimitKey.computeKey(resourceInfo,containerRequestContext);
          if(null != rateLimiterKey) {
            RateLimiter rateLimiter = rateLimitManager.getRateLimiter(rateLimiterKey);
            if(rateLimitKey.getRateLimitAttribute() == RateLimitAttribute.HEADER && rateLimiter == null){
                throw new WebApplicationException("Rate limit not configured for " + rateLimitKey.getRateLimitAttribute() + " " + rateLimitKey.getAttributeValue() + " " + rateLimiterKey, Response.status(Response.Status.FORBIDDEN).build());
            }
            if(null != rateLimiter){
              if(!rateLimiter.tryAcquire()){
                Exception cause = new IllegalAccessException("Too many requests trying to access "+resourceInfo.getResourceMethod().getName());
                throw new WebApplicationException(cause, Response.status(TOO_MANY_REQUESTS).build());
              }
            }
          }
        }
      }
    }
}
