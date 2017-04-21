package in.adavi.pradyot.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import in.adavi.pradyot.annotation.RateLimit;
import in.adavi.pradyot.core.RateLimitBundleConfiguration;
import in.adavi.pradyot.core.RateLimitManager;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import static in.adavi.pradyot.core.Util.*;


/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitRegistration implements DynamicFeature {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitRegistration.class);

    private RateLimitManager rateLimitManager;
    private RateLimitBundleConfiguration rateLimitBundleConfiguration;

    @Inject
    public RateLimitRegistration(RateLimitManager rateLimitManager, RateLimitBundleConfiguration rateLimitBundleConfiguration) {
        this.rateLimitManager = rateLimitManager;
        this.rateLimitBundleConfiguration = rateLimitBundleConfiguration;
    }

    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {

        final AnnotatedMethod method = new AnnotatedMethod(resourceInfo.getResourceMethod());
        final RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        if(null != rateLimit)
        {
            try {
                validate(rateLimit);
            }catch (IllegalStateException ex){
                String exceptionMsg = ex.getMessage()+" for "+method.getMethod().getName();
                throw new IllegalStateException(exceptionMsg);
            }
            Double permits;
            String rateLimiterKey = null;
            String globalKey = rateLimit.permitsGlobalKey();
            if(hasGlobalPermit(globalKey))
            {
                permits = rateLimitBundleConfiguration.getGlobalPermits(globalKey);
                rateLimiterKey = globalKey;
            } else {
                permits = rateLimit.permits();
                rateLimiterKey = method.getMethod().getName();
            }
            RateLimiter rateLimiter = RateLimiter.create(permits, rateLimit.warmUpPeriod(), rateLimit.timeUnit());
            rateLimitManager.setRateLimiter(rateLimiterKey, rateLimiter);
            featureContext.register(new RateLimitFilter(resourceInfo,rateLimitManager));
        }
    }

    void validate(RateLimit rateLimit) throws IllegalStateException{

        Double permits = rateLimit.permits();
        String globalKey = rateLimit.permitsGlobalKey();

        /**
         * Neither global key nor local permits specified
         */
        if(isZero(permits) && isEmpty(globalKey)){
            String exceptionMsg = "Neither global key nor local permits specified";
            throw new IllegalStateException(exceptionMsg);
        }

        /**
         * Both global key and local permits specified
         */
        if(notZero(permits) && hasGlobalPermit(globalKey)){
            String exceptionMsg = "Both global key and local permits specified";
            throw new IllegalStateException(exceptionMsg);
        }

        /**
         * Global permits value missing from configuration but global key specified
         */
        if(isNotEmpty(globalKey) && !hasGlobalPermit(globalKey)){
            String exceptionMsg = "Global permits value missing from configuration but global key specified";
            throw new IllegalStateException(exceptionMsg);
        }
    }

    boolean hasGlobalPermit(String value){
        if(rateLimitBundleConfiguration.containsKey(value))
            return true;
        return false;
    }
}
