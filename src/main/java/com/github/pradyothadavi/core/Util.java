package com.github.pradyothadavi.core;

import com.github.pradyothadavi.annotation.RateLimit;
import com.github.pradyothadavi.annotation.RateLimitByGroup;
import com.github.pradyothadavi.annotation.RateLimitByHeader;
import com.github.pradyothadavi.annotation.RateLimitByNamedHeader;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public class Util {

    public static boolean isEmpty(String value){
        if("".equals(value) || null == value)
            return true;
        return false;
    }

    public static boolean isNotEmpty(String value){
        return !isEmpty(value);
    }

    public static boolean isZero(Double value){
        Double zero = new Double(0.0);
        if(0 == zero.compareTo(value))
            return true;
        return false;
    }

    public static boolean notZero(Double value){
        return !isZero(value);
    }

    public static boolean isPresent(RateLimit rateLimit){
        return !(null == rateLimit);
    }

    public static boolean isPresent(RateLimitByGroup rateLimitByGroup){
        return!(null == rateLimitByGroup);
    }

    public static boolean isPresent(RateLimitByHeader rateLimitByHeader){
        return !(null == rateLimitByHeader);
    }

    public static boolean isPresent(RateLimitByNamedHeader rateLimitByNamedHeader){
        return !(null == rateLimitByNamedHeader);
    }
}
