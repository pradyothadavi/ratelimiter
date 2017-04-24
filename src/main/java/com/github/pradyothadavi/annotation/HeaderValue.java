package com.github.pradyothadavi.annotation;

import com.github.pradyothadavi.core.RateLimitBundleConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate limiting based on Header Values
 * Created by pradyot.ha on 24/04/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface HeaderValue {

    /**
     * Value of the header mentioned in {@link RateLimitByHeader#header()}
     * @return
     */
    String value();

    /**
     * Rate per second
     * @return
     */
    double ratePerSecond() default 0;

    /**
     * Named alias for Rate per second which is present in {@link RateLimitBundleConfiguration#namedLimits}
     * @return
     */
    String nameLimit() default "";
}
