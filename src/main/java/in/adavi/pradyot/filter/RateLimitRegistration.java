package in.adavi.pradyot.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import in.adavi.pradyot.annotation.ClientParam;
import in.adavi.pradyot.annotation.RateLimit;
import in.adavi.pradyot.annotation.RateParam;
import in.adavi.pradyot.core.RateLimitBundleConfiguration;
import in.adavi.pradyot.core.RateLimitManager;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import java.util.ArrayList;
import java.util.List;

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
            RateParam rateParam = rateLimit.rateParam();
            if(hasGlobalPermit(globalKey))
            {
                if(hasClientRateParam(rateParam))
                {
                    List<ClientParam> clientParams = new ArrayList<ClientParam>();
                    ClientParam[] annoClientParams = rateParam.clients();
                    for (ClientParam clientParam : annoClientParams) {
                        clientParams.add(clientParam);
                    }
                } else {
                    permits = rateLimitBundleConfiguration.getGlobalPermits(globalKey);
                    rateLimiterKey = globalKey;
                    RateLimiter rateLimiter = RateLimiter.create(permits, rateLimit.warmUpPeriod(), rateLimit.timeUnit());
                    rateLimitManager.setRateLimiter(rateLimiterKey, rateLimiter);
                }
            } else {

                if(hasClientRateParam(rateParam))
                {
                    ClientParam[] annoClientParams = rateParam.clients();
                    for (ClientParam clientParam : annoClientParams) {
                        permits = (rateLimit.localPermits()*clientParam.percent())/100;
                        rateLimiterKey = method.getMethod().getName()+":"+ clientParam.name();
                        RateLimiter rateLimiter = RateLimiter.create(permits, rateLimit.warmUpPeriod(), rateLimit.timeUnit());
                        rateLimitManager.setRateLimiter(rateLimiterKey, rateLimiter);
                    }
                } else {
                    permits = rateLimit.localPermits();
                    rateLimiterKey = method.getMethod().getName();
                    RateLimiter rateLimiter = RateLimiter.create(permits, rateLimit.warmUpPeriod(), rateLimit.timeUnit());
                    rateLimitManager.setRateLimiter(rateLimiterKey, rateLimiter);
                }
            }
            featureContext.register(new RateLimitFilter(resourceInfo,rateLimitManager));
        }
    }

    void validate(RateLimit rateLimit) throws IllegalStateException{

        Double permits = rateLimit.localPermits();
        String globalKey = rateLimit.permitsGlobalKey();

        /**
         * Neither global key nor local permits specified
         */
        if(isZero(permits) && isEmpty(globalKey)){
            String exceptionMsg = "Neither global key nor local localPermits specified";
            throw new IllegalStateException(exceptionMsg);
        }

        /**
         * Both global key and local permits specified
         */
        if(notZero(permits) && hasGlobalPermit(globalKey)){
            String exceptionMsg = "Both global key and local localPermits specified";
            throw new IllegalStateException(exceptionMsg);
        }

        /**
         * Global localPermits value missing from configuration but global key specified
         */
        if(isNotEmpty(globalKey) && !hasGlobalPermit(globalKey)){
            String exceptionMsg = "Global localPermits value missing from configuration but global key specified";
            throw new IllegalStateException(exceptionMsg);
        }
    }

    boolean hasGlobalPermit(String value){
        if(rateLimitBundleConfiguration.containsKey(value))
            return true;
        return false;
    }

    boolean hasDefaultClient(RateParam rateParam){
        ClientParam[] clientParams = rateParam.clients();
        if(1 == clientParams.length && isEmpty(clientParams[0].name()))
            return true;
        return false;
    }

    boolean hasClientRateParam(RateParam rateParam){
        return !hasDefaultClient(rateParam);
    }
}
