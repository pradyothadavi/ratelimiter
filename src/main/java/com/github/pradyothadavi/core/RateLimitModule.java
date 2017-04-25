package com.github.pradyothadavi.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitModule extends AbstractModule {

    private RateLimitBundleConfiguration rateLimitBundleConfiguration;

    protected void configure() {

    }

    public RateLimitModule(RateLimitBundleConfiguration rateLimitBundleConfiguration) {
        this.rateLimitBundleConfiguration = rateLimitBundleConfiguration;
    }

    @Provides
    @Singleton
    public RateLimitManager rateLimitManagerProvider(){
        return new RateLimitManager();
    }
    @Provides
    @Singleton
    public RateLimitBundleConfiguration rateLimitBundleConfigurationProvider(){
        return rateLimitBundleConfiguration;
    }
}
