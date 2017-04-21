package in.adavi.pradyot.filter;

import com.google.common.util.concurrent.RateLimiter;
import in.adavi.pradyot.annotation.RateLimit;
import in.adavi.pradyot.core.RateLimitManager;
import org.glassfish.jersey.server.model.AnnotatedMethod;

import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitRegistration implements DynamicFeature {

    private RateLimitManager rateLimitManager;

    @Inject
    public RateLimitRegistration(RateLimitManager rateLimitManager) {
        this.rateLimitManager = rateLimitManager;
    }

    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {

        final AnnotatedMethod method = new AnnotatedMethod(resourceInfo.getResourceMethod());
        final RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if(null != rateLimit)
        {
            RateLimiter rateLimiter = RateLimiter.create(rateLimit.permits(), rateLimit.warmUpPeriod(), rateLimit.timeUnit());
            rateLimitManager.setRateLimiterMap(method.getMethod().getName(),rateLimiter);
            featureContext.register(new RateLimitFilter(resourceInfo,rateLimitManager));
        }
    }
}
