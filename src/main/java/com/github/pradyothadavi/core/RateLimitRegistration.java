package com.github.pradyothadavi.core;

import com.github.pradyothadavi.annotation.*;
import com.github.pradyothadavi.core.configuration.HeaderValueLimitMap;
import com.github.pradyothadavi.core.configuration.RateLimitBundleConfiguration;
import com.github.pradyothadavi.filter.RateLimitFilter;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import org.glassfish.jersey.server.model.AnnotatedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.Map;


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
    RateLimitByNamedHeader rateLimitByNamedHeader = method.getAnnotation(RateLimitByNamedHeader.class);

    try {
      validate(rateLimit, rateLimitByGroup, rateLimitByHeader, rateLimitByNamedHeader);
    } catch (IllegalStateException ex) {
      if (method.getMethod().getName().equals("apply")) {
        logger.warn(ex.getMessage() + " for method " + method.getMethod().getName());
      } else {
        throw new IllegalStateException(ex.getMessage() + " for method " + method.getMethod().getName() + " in " + resourceInfo.getResourceClass().getCanonicalName());
      }
    }

    if (Util.isPresent(rateLimit)) {
      registerRateLimit(method, rateLimit);
      featureContext.register(new RateLimitFilter(resourceInfo, rateLimitManager));
    }

    if (Util.isPresent(rateLimitByGroup)) {
      registerRateLimitByGroup(method, rateLimitByGroup);
      featureContext.register(new RateLimitFilter(resourceInfo, rateLimitManager));
    }

    if (Util.isPresent(rateLimitByHeader)) {
      registerRateLimitByHeader(method, rateLimitByHeader);
      featureContext.register(new RateLimitFilter(resourceInfo, rateLimitManager));
    }

    if (Util.isPresent(rateLimitByNamedHeader)) {
        registerRateLimitByNamedHeader(method, rateLimitByNamedHeader);
        featureContext.register(new RateLimitFilter(resourceInfo, rateLimitManager));
    }
  }

  private void registerRateLimitByNamedHeader(AnnotatedMethod method, RateLimitByNamedHeader rateLimitByNamedHeader) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.HEADER);
    HeaderValueLimitMap headerValueLimitMap = rateLimitBundleConfiguration.getNamedHeaderLimits().get(rateLimitByNamedHeader.value());
    if(headerValueLimitMap == null){
        throw new IllegalStateException("namedHeaderLimits with name " + rateLimitByNamedHeader.value() + " not found in configuration for method" + method.getMethod().getName());
    }

    rateLimitKey.setAttributeValue(headerValueLimitMap.getHeader());

    String key = null;
    RateLimiter rateLimiter = null;
    Map<String, Double> rateLimits = headerValueLimitMap.getLimits();
    for (Map.Entry<String, Double> limit : rateLimits.entrySet()) {
        key = method.getMethod().getName() + Constant.COLON + limit.getKey();
        rateLimiter = RateLimiter.create(limit.getValue());
        rateLimitManager.setRateLimiter(key, rateLimiter);
    }

    rateLimitManager.setRateLimitKey(method.getMethod().getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getMethod().getName());
  }

  private void registerRateLimitByHeader(AnnotatedMethod method, RateLimitByHeader rateLimitByHeader) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.HEADER);
    rateLimitKey.setAttributeValue(rateLimitByHeader.header());

    String key = null;
    RateLimiter rateLimiter = null;
    HeaderValue[] rateLimits = rateLimitByHeader.rateLimits();
    for (HeaderValue headerValue : rateLimits) {

      boolean atmostOneParam = Util.isEmpty(headerValue.nameLimit()) ^ Util.isZero(headerValue.ratePerSecond());
      if (!atmostOneParam) {
        throw new IllegalStateException("Atmost one param(nameLimit and ratePerSecond) must be specified for @HeaderValue in method " + method.getMethod().getName());
      }
      key = method.getMethod().getName() + Constant.COLON + headerValue.value();
      if (Util.isNotEmpty(headerValue.nameLimit())) {
        rateLimiter = RateLimiter.create(rateLimitBundleConfiguration.getNamedLimit(headerValue.nameLimit()));
      }
      if (Util.notZero(headerValue.ratePerSecond())) {
        rateLimiter = RateLimiter.create(headerValue.ratePerSecond());
      }
      rateLimitManager.setRateLimiter(key, rateLimiter);
    }
    rateLimitManager.setRateLimitKey(method.getMethod().getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getMethod().getName());
  }

  private void registerRateLimitByGroup(AnnotatedMethod method, RateLimitByGroup rateLimitByGroup) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.GROUP);
    rateLimitKey.setAttributeValue(rateLimitByGroup.value());

    RateLimiter rateLimiter = RateLimiter.create(rateLimitBundleConfiguration.getGroupLimit(rateLimitByGroup.value()));
    String key = method.getMethod().getName() + Constant.COLON + rateLimitByGroup.value();

    rateLimitManager.setRateLimiter(key, rateLimiter);
    rateLimitManager.setRateLimitKey(method.getMethod().getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getMethod().getName());
  }

  private void registerRateLimit(AnnotatedMethod method, RateLimit rateLimit) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.RPS);

    RateLimiter rateLimiter = RateLimiter.create(rateLimit.ratePerSecond());
    String key = method.getMethod().getName();

    rateLimitManager.setRateLimiter(key, rateLimiter);
    rateLimitManager.setRateLimitKey(method.getMethod().getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getMethod().getName());
  }

  private void validate(RateLimit rateLimit, RateLimitByGroup rateLimitByGroup, RateLimitByHeader rateLimitByHeader, RateLimitByNamedHeader rateLimitByNamedHeader) {
    boolean valid = false;

    boolean a = Util.isPresent(rateLimit);
    boolean b = Util.isPresent(rateLimitByGroup);
    boolean c = Util.isPresent(rateLimitByHeader);
    boolean d = Util.isPresent(rateLimitByNamedHeader);

    if ((a && !b && !c && !d) || (!a && b && !c && !d) || (!a && !b && c && !d) || (!a && !b && !c && d) || (!a && !b && !c && !d)) {
      valid = true;
    }
    if (!valid) {
      throw new IllegalStateException("At most one annotation needs to be specified among [@RateLimit, @RateLimitByGroup, @RateLimitByHeader and @RateLimitByNamedHeader]");
    }
  }


}
