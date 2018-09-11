package com.github.pradyothadavi;

import com.github.pradyothadavi.core.configuration.RateLimitBundleConfiguration;
import com.github.pradyothadavi.core.RateLimitManager;
import com.github.pradyothadavi.core.RateLimitModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.github.pradyothadavi.core.DisplayRateLimiterTask;
import com.github.pradyothadavi.core.RateLimitRegistration;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public abstract class RateLimitBundle<T extends Configuration> implements ConfiguredBundle<T> {

    public void run(final T configuration,final Environment environment) throws Exception {

        RateLimitBundleConfiguration rateLimitBundleConfiguration = getRateLimitBundleConfiguration(configuration);
        if(null == rateLimitBundleConfiguration)
        {
            rateLimitBundleConfiguration = new RateLimitBundleConfiguration();
        }
        Injector injector = Guice.createInjector(new RateLimitModule(rateLimitBundleConfiguration));
    }

    public void initialize(Bootstrap<?> bootstrap) {

    }

    protected abstract RateLimitBundleConfiguration getRateLimitBundleConfiguration(T configuration);
}
