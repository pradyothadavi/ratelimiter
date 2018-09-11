package com.github.pradyothadavi.filter;

import com.github.pradyothadavi.annotation.RateLimit;
import com.github.pradyothadavi.annotation.RateLimitByGroup;
import com.github.pradyothadavi.annotation.RateLimitByHeader;
import com.github.pradyothadavi.annotation.RateLimitByNamedHeader;
import com.github.pradyothadavi.core.RateLimitAttribute;
import com.github.pradyothadavi.core.RateLimitKey;
import com.github.pradyothadavi.core.RateLimitManager;
import com.google.common.reflect.ClassPath;
import com.google.common.util.concurrent.RateLimiter;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    private AbstractMethod abstractMethod;

    @Context
    ExtendedUriInfo extendedUriInfo;

    private int TOO_MANY_REQUESTS = 429;
    private RateLimitManager rateLimitManager;

    public RateLimitFilter(RateLimitManager rateLimitManager, AbstractMethod abstractMethod) {
        this.abstractMethod = abstractMethod;
        this.rateLimitManager = rateLimitManager;
    }

    public ContainerRequest filter(ContainerRequest containerRequest) {
        if (!abstractMethod.isAnnotationPresent(RateLimit.class)
                && !abstractMethod.isAnnotationPresent(RateLimitByGroup.class)
                && !abstractMethod.isAnnotationPresent(RateLimitByHeader.class)
                && !abstractMethod.isAnnotationPresent(RateLimitByNamedHeader.class)) {
            return containerRequest;
        }


        RateLimitKey rateLimitKey = rateLimitManager.getRateLimitKey(abstractMethod.getMethod().getName());
        if (null != rateLimitKey) {
            String rateLimiterKey = rateLimitKey.computeKey(abstractMethod.getMethod(), containerRequest);
            if (null != rateLimiterKey) {
                RateLimiter rateLimiter = rateLimitManager.getRateLimiter(rateLimiterKey);
                if (rateLimitKey.getRateLimitAttribute() == RateLimitAttribute.HEADER && rateLimiter == null) {
                    throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("Rate limit not configured for " + rateLimitKey.getRateLimitAttribute() + " " + rateLimitKey.getAttributeValue() + " " + rateLimiterKey).build());
                }
                if (null != rateLimiter) {
                    if (!rateLimiter.tryAcquire()) {
                        Exception cause = new IllegalAccessException("Too many requests trying to access " + abstractMethod.getMethod().getName());
                        throw new WebApplicationException(cause, Response.status(TOO_MANY_REQUESTS).build());
                    }
                }
            }
        }
        return containerRequest;
    }
}
