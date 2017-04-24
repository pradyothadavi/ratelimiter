package com.github.pradyothadavi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by pradyot.ha on 24/04/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface RateLimitByHeader {

    String header();

    HeaderValue[] rateLimits();
}
