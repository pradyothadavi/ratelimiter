package com.github.pradyothadavi.annotation;

import com.github.pradyothadavi.core.configuration.RateLimitBundleConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate limiting based on group
 * Created by pradyot.ha on 24/04/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface RateLimitByGroup {

    /**
     * Value is present in {@link RateLimitBundleConfiguration#groupLimits}
     * @return
     */
    String value();
}
