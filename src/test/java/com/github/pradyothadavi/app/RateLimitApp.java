package com.github.pradyothadavi.app;

import com.github.pradyothadavi.RateLimitBundle;
import com.github.pradyothadavi.app.web.RateLimitByGroupDemoResource;
import com.github.pradyothadavi.app.web.RateLimitByHeaderDemoResource;
import com.github.pradyothadavi.app.web.RateLimitConfiguration;
import com.github.pradyothadavi.app.web.RateLimitDemoResource;
import com.github.pradyothadavi.core.configuration.RateLimitBundleConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Created by pradyot.ha on 24/04/17.
 */
public class RateLimitApp extends Application<RateLimitConfiguration> {

    @Override
    public void initialize(Bootstrap<RateLimitConfiguration> bootstrap) {
        super.initialize(bootstrap);

        bootstrap.addBundle(new RateLimitBundle<RateLimitConfiguration>() {
            @Override
            protected RateLimitBundleConfiguration getRateLimitBundleConfiguration(RateLimitConfiguration configuration) {
                return configuration.getRateLimitBundleConfiguration();
            }
        });
    }

    public void run(RateLimitConfiguration rateLimitConfiguration, Environment environment) throws Exception {
        environment.jersey().register(new RateLimitDemoResource());
        environment.jersey().register(new RateLimitByGroupDemoResource());
        environment.jersey().register(new RateLimitByHeaderDemoResource());
    }
}
