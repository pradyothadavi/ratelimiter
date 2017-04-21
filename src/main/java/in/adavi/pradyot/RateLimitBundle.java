package in.adavi.pradyot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import in.adavi.pradyot.core.RateLimitModule;
import in.adavi.pradyot.filter.RateLimitRegistration;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitBundle implements ConfiguredBundle<Configuration> {

    public void run(final Configuration configuration,final Environment environment) throws Exception {

        Injector injector = Guice.createInjector(new RateLimitModule());
        environment.jersey().register(injector.getInstance(RateLimitRegistration.class));
    }

    public void initialize(Bootstrap<?> bootstrap) {

    }
}
