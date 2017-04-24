package com.github.pradyothadavi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate limiting based on the value mentioned
 * Created by pradyot.ha on 20/04/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface RateLimit {

    double ratePerSecond();
}
