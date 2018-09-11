package com.github.pradyothadavi.core;

import com.github.pradyothadavi.annotation.*;
import com.github.pradyothadavi.core.configuration.HeaderValueLimitMap;
import com.github.pradyothadavi.core.configuration.RateLimitBundleConfiguration;
import com.github.pradyothadavi.filter.RateLimitFilter;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitRegistration implements ResourceFilterFactory {

  private static final Logger logger = LoggerFactory.getLogger(RateLimitRegistration.class);

  private RateLimitManager rateLimitManager;
  private RateLimitBundleConfiguration rateLimitBundleConfiguration;

  @Inject
  public RateLimitRegistration(RateLimitManager rateLimitManager, RateLimitBundleConfiguration rateLimitBundleConfiguration) {
    this.rateLimitManager = rateLimitManager;
    this.rateLimitBundleConfiguration = rateLimitBundleConfiguration;
  }

  public List<ResourceFilter> create(AbstractMethod abstractMethod) {
    Method method = abstractMethod.getMethod();

    RateLimit rateLimit = method.getAnnotation(RateLimit.class);
    RateLimitByGroup rateLimitByGroup = method.getAnnotation(RateLimitByGroup.class);
    RateLimitByHeader rateLimitByHeader = method.getAnnotation(RateLimitByHeader.class);
    RateLimitByNamedHeader rateLimitByNamedHeader = method.getAnnotation(RateLimitByNamedHeader.class);

    try {
      validate(rateLimit, rateLimitByGroup, rateLimitByHeader, rateLimitByNamedHeader);
    } catch (IllegalStateException ex) {
      if (method.getName().equals("apply")) {
        logger.warn(ex.getMessage() + " for method " + method.getName());
      } else {
        throw new IllegalStateException(ex.getMessage() + " for method " + method.getName() + " in " + method.getDeclaringClass());
      }
    }

    if (Util.isPresent(rateLimit)) {
      registerRateLimit(method, rateLimit);
      return Arrays.asList(getResourceFilter(new RateLimitFilter(rateLimitManager, abstractMethod)));
    }

    if (Util.isPresent(rateLimitByGroup)) {
      registerRateLimitByGroup(method, rateLimitByGroup);
      return Arrays.asList(getResourceFilter(new RateLimitFilter(rateLimitManager, abstractMethod)));
    }

    if (Util.isPresent(rateLimitByHeader)) {
      registerRateLimitByHeader(method, rateLimitByHeader);
      return Arrays.asList(getResourceFilter(new RateLimitFilter(rateLimitManager, abstractMethod)));
    }

    if (Util.isPresent(rateLimitByNamedHeader)) {
      registerRateLimitByNamedHeader(method, rateLimitByNamedHeader);
      return Arrays.asList(getResourceFilter(new RateLimitFilter(rateLimitManager, abstractMethod)));
    }

    return Arrays.asList();
  }

  private ResourceFilter getResourceFilter(RateLimitFilter rateLimitFilter){
    final RateLimitFilter rateLimitFilterFinal = rateLimitFilter;
    ResourceFilter resourceFilter = new ResourceFilter() {
      public ContainerRequestFilter getRequestFilter() {
        return rateLimitFilterFinal;
      }

      public ContainerResponseFilter getResponseFilter() {
        return null;
      }
    };

    return resourceFilter;
  }

  private void registerRateLimitByNamedHeader(Method method, RateLimitByNamedHeader rateLimitByNamedHeader) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.HEADER);
    HeaderValueLimitMap headerValueLimitMap = rateLimitBundleConfiguration.getNamedHeaderLimits().get(rateLimitByNamedHeader.value());
    if(headerValueLimitMap == null){
        throw new IllegalStateException("namedHeaderLimits with name " + rateLimitByNamedHeader.value() + " not found in configuration for method" + method.getName());
    }

    rateLimitKey.setAttributeValue(headerValueLimitMap.getHeader());

    String key = null;
    RateLimiter rateLimiter = null;
    Map<String, Double> rateLimits = headerValueLimitMap.getLimits();
    for (Map.Entry<String, Double> limit : rateLimits.entrySet()) {
        key = method.getName() + Constant.COLON + limit.getKey();
        rateLimiter = RateLimiter.create(limit.getValue());
        rateLimitManager.setRateLimiter(key, rateLimiter);
    }

    rateLimitManager.setRateLimitKey(method.getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getName());
  }

  private void registerRateLimitByHeader(Method method, RateLimitByHeader rateLimitByHeader) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.HEADER);
    rateLimitKey.setAttributeValue(rateLimitByHeader.header());

    String key = null;
    RateLimiter rateLimiter = null;
    HeaderValue[] rateLimits = rateLimitByHeader.rateLimits();
    for (HeaderValue headerValue : rateLimits) {

      boolean atmostOneParam = Util.isEmpty(headerValue.nameLimit()) ^ Util.isZero(headerValue.ratePerSecond());
      if (!atmostOneParam) {
        throw new IllegalStateException("Atmost one param(nameLimit and ratePerSecond) must be specified for @HeaderValue in method " + method.getName());
      }
      key = method.getName() + Constant.COLON + headerValue.value();
      if (Util.isNotEmpty(headerValue.nameLimit())) {
        rateLimiter = RateLimiter.create(rateLimitBundleConfiguration.getNamedLimit(headerValue.nameLimit()));
      }
      if (Util.notZero(headerValue.ratePerSecond())) {
        rateLimiter = RateLimiter.create(headerValue.ratePerSecond());
      }
      rateLimitManager.setRateLimiter(key, rateLimiter);
    }
    rateLimitManager.setRateLimitKey(method.getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getName());
  }

  private void registerRateLimitByGroup(Method method, RateLimitByGroup rateLimitByGroup) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.GROUP);
    rateLimitKey.setAttributeValue(rateLimitByGroup.value());

    RateLimiter rateLimiter = RateLimiter.create(rateLimitBundleConfiguration.getGroupLimit(rateLimitByGroup.value()));
    String key = method.getName() + Constant.COLON + rateLimitByGroup.value();

    rateLimitManager.setRateLimiter(key, rateLimiter);
    rateLimitManager.setRateLimitKey(method.getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getName());
  }

  private void registerRateLimit(Method method, RateLimit rateLimit) {
    RateLimitKey rateLimitKey = new RateLimitKey();
    rateLimitKey.setRateLimitAttribute(RateLimitAttribute.RPS);

    RateLimiter rateLimiter = RateLimiter.create(rateLimit.ratePerSecond());
    String key = method.getName();

    rateLimitManager.setRateLimiter(key, rateLimiter);
    rateLimitManager.setRateLimitKey(method.getName(), rateLimitKey);
    logger.info("Key : {} RateLimiter : {} for method : {}", key, rateLimiter, method.getName());
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
