package in.adavi.pradyot.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by pradyot.ha on 20/04/17.
 */
public class RateLimitModule extends AbstractModule {

    protected void configure() {

    }

    @Provides
    @Singleton
    public RateLimitManager rateLimitManagerProvider(){
        return new RateLimitManager();
    }
}
