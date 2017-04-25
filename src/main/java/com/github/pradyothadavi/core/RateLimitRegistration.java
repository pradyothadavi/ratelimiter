package com.github.pradyothadavi.core;

import com.github.pradyothadavi.annotation.HeaderValue;
import com.github.pradyothadavi.annotation.RateLimit;
import com.github.pradyothadavi.annotation.RateLimitByHeader;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import com.github.pradyothadavi.annotation.RateLimitByGroup;
import com.github.pradyothadavi.filter.RateLimitFilter;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;


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

        AnnotatedMethod method = new AnnotatedMethod(resourceInfo.getResourceMethod());

        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        RateLimitByGroup rateLimitByGroup = method.getAnnotation(RateLimitByGroup.class);
        RateLimitByHeader rateLimitByHeader = method.getAnnotation(RateLimitByHeader.class);

        try {
            validate(rateLimit,rateLimitByGroup,rateLimitByHeader);
        } catch (IllegalStateException ex){
            if(method.getMethod().getName().equals("apply"))
            {
                logger.warn(ex.getMessage()+" for method "+method.getMethod().getName());
            } else {
                throw new IllegalStateException(ex.getMessage()+" for method "+method.getMethod().getName()+" in "+resourceInfo.getResourceClass().getCanonicalName());
            }
        }

        if(Util.isPresent(rateLimit)){
            registerRateLimit(method, rateLimit);
        }

        if(Util.isPresent(rateLimitByGroup)){
            registerRateLimitByGroup(method, rateLimitByGroup);
        }

        if(Util.isPresent(rateLimitByHeader)){
            registerRateLimitByHeader(method, rateLimitByHeader);
        }
        featureContext.register(new RateLimitFilter(resourceInfo, rateLimitManager));
    }

    private void registerRateLimitByHeader(AnnotatedMethod method, RateLimitByHeader rateLimitByHeader) {
        RateLimitKey rateLimitKey = new RateLimitKey();
        rateLimitKey.setRateLimitAttribute(RateLimitAttribute.HEADER);
        rateLimitKey.setAttributeValue(rateLimitByHeader.header());

        String key = null;
        RateLimiter rateLimiter = null;
        HeaderValue[] rateLimits = rateLimitByHeader.rateLimits();
        for (HeaderValue headerValue: rateLimits) {

            boolean atmostOneParam = Util.isEmpty(headerValue.nameLimit()) ^ Util.isZero(headerValue.ratePerSecond());
            if(!atmostOneParam){
                throw new IllegalStateException("Atmost one param(nameLimit and ratePerSecond) must be specified for @HeaderValue in method "+method.getMethod().getName());
            }
            key = method.getMethod().getName()+ Constant.COLON+headerValue.value();
            if(Util.isNotEmpty(headerValue.nameLimit())) {
                rateLimiter = RateLimiter.create(rateLimitBundleConfiguration.getNamedLimit(headerValue.nameLimit()));
            }
            if(Util.notZero(headerValue.ratePerSecond())){
                rateLimiter = RateLimiter.create(headerValue.ratePerSecond());
            }
            rateLimitManager.setRateLimiter(key,rateLimiter);
        }
        rateLimitManager.setRateLimitKey(method.getMethod().getName(),rateLimitKey);
        logger.info("Key : {} RateLimiter : {} for method : {}",key,rateLimiter,method.getMethod().getName());
    }

    private void registerRateLimitByGroup(AnnotatedMethod method, RateLimitByGroup rateLimitByGroup) {
        RateLimitKey rateLimitKey = new RateLimitKey();
        rateLimitKey.setRateLimitAttribute(RateLimitAttribute.GROUP);
        rateLimitKey.setAttributeValue(rateLimitByGroup.value());

        RateLimiter rateLimiter = RateLimiter.create(rateLimitBundleConfiguration.getGroupLimit(rateLimitByGroup.value()));
        String key = method.getMethod().getName()+ Constant.COLON+rateLimitByGroup.value();

        rateLimitManager.setRateLimiter(key,rateLimiter);
        rateLimitManager.setRateLimitKey(method.getMethod().getName(),rateLimitKey);
        logger.info("Key : {} RateLimiter : {} for method : {}",key,rateLimiter,method.getMethod().getName());
    }

    private void registerRateLimit(AnnotatedMethod method, RateLimit rateLimit) {
        RateLimitKey rateLimitKey = new RateLimitKey();
        rateLimitKey.setRateLimitAttribute(RateLimitAttribute.RPS);

        RateLimiter rateLimiter = RateLimiter.create(rateLimit.ratePerSecond());
        String key = method.getMethod().getName();

        rateLimitManager.setRateLimiter(key,rateLimiter);
        rateLimitManager.setRateLimitKey(method.getMethod().getName(),rateLimitKey);
        logger.info("Key : {} RateLimiter : {} for method : {}",key,rateLimiter,method.getMethod().getName());
    }

    private void validate(RateLimit rateLimit, RateLimitByGroup rateLimitByGroup, RateLimitByHeader rateLimitByHeader) {
        boolean valid = false;
        if(!Util.isPresent(rateLimit) && !Util.isPresent(rateLimitByGroup) && !Util.isPresent(rateLimitByHeader) ||
            !Util.isPresent(rateLimit) && !Util.isPresent(rateLimitByGroup) && Util.isPresent(rateLimitByHeader) ||
            !Util.isPresent(rateLimit) && Util.isPresent(rateLimitByGroup) && !Util.isPresent(rateLimitByHeader) ||
            Util.isPresent(rateLimit) && !Util.isPresent(rateLimitByGroup) && !Util.isPresent(rateLimitByHeader)){
                valid = true;
        }
        if(!valid){
            throw new IllegalStateException("Atmost one annotation needs to be specified among [@RateLimit, @RateLimitByGroup and @RateLimitByHeader]");
        }
    }


}
