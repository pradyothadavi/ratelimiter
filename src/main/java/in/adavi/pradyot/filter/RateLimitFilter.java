package in.adavi.pradyot.filter;

import com.google.common.util.concurrent.RateLimiter;
import in.adavi.pradyot.annotation.RateLimit;
import in.adavi.pradyot.core.RateLimitManager;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static in.adavi.pradyot.core.Util.hasClientRateParam;
import static in.adavi.pradyot.core.Util.isNotEmpty;

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

        final AnnotatedMethod method = new AnnotatedMethod(resourceInfo.getResourceMethod());
        final RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if(null != rateLimit) {
            String rateLimiterKey = null;
            if(isNotEmpty(rateLimit.permitsGroupKey()))
            {
                rateLimiterKey = rateLimit.permitsGroupKey();
            } else {
                if(hasClientRateParam(rateLimit.rateParam())){
                    String clientId = containerRequestContext.getHeaderString("X-Client-Id");
                    rateLimiterKey = method.getMethod().getName()+":"+clientId;
                } else {
                    rateLimiterKey = method.getMethod().getName();
                }
            }
            RateLimiter rateLimiter = rateLimitManager.getRateLimiter(rateLimiterKey);
            if(null != rateLimiter){
                if(!rateLimiter.tryAcquire()){
                    Exception cause = new IllegalAccessException("Too many requests trying to access "+method.getMethod().getName());
                    throw new WebApplicationException(cause, Response.status(TOO_MANY_REQUESTS).build());
                }
            }
        }
    }
}
