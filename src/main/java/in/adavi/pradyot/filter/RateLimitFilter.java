package in.adavi.pradyot.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import in.adavi.pradyot.core.RateLimitManager;
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

    private ResourceInfo resourceInfo;
    private RateLimitManager rateLimitManager;

    @Inject
    public RateLimitFilter(ResourceInfo resourceInfo, RateLimitManager rateLimitManager) {
        this.resourceInfo = resourceInfo;
        this.rateLimitManager = rateLimitManager;
    }

    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        RateLimiter rateLimiter = rateLimitManager.getRateLimiter(resourceInfo.getResourceMethod().getName());
        if(null != rateLimiter){
            logger.info(rateLimiter.toString());
            if(!rateLimiter.tryAcquire()){
                Exception cause = new IllegalAccessException("Too many requests.");
                throw new WebApplicationException(cause, Response.Status.fromStatusCode(429));
            }
        }
    }
}
