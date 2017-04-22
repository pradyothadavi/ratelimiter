package in.adavi.pradyot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import in.adavi.pradyot.core.DisplayRateLimiterTask;
import in.adavi.pradyot.core.RateLimitBundleConfiguration;
import in.adavi.pradyot.core.RateLimitManager;
import in.adavi.pradyot.core.RateLimitModule;
import in.adavi.pradyot.filter.RateLimitRegistration;
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

        Injector injector = Guice.createInjector(new RateLimitModule(rateLimitBundleConfiguration));
        environment.jersey().register(injector.getInstance(RateLimitRegistration.class));

        environment.admin().addTask(new DisplayRateLimiterTask(injector.getInstance(RateLimitManager.class)));

        environment.lifecycle().manage(injector.getInstance(RateLimitManager.class));
    }

    public void initialize(Bootstrap<?> bootstrap) {

    }

    protected abstract RateLimitBundleConfiguration getRateLimitBundleConfiguration(T configuration);
}
