package com.github.pradyothadavi.app.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pradyothadavi.core.RateLimitBundleConfiguration;
import io.dropwizard.Configuration;

/**
 * Created by pradyot.ha on 24/04/17.
 */
public class RateLimitConfiguration extends Configuration {

    private RateLimitBundleConfiguration rateLimitBundleConfiguration;

    @JsonProperty("rateLimiter")
    public RateLimitBundleConfiguration getRateLimitBundleConfiguration() {
        return rateLimitBundleConfiguration;
    }

    public void setRateLimitBundleConfiguration(RateLimitBundleConfiguration rateLimitBundleConfiguration) {
        this.rateLimitBundleConfiguration = rateLimitBundleConfiguration;
    }
}
