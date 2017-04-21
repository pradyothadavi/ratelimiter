package in.adavi.pradyot.core;

import com.google.common.util.concurrent.RateLimiter;
import io.dropwizard.lifecycle.Managed;

import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradyot.ha on 20/04/17.
 */
@Provider
public class RateLimitManager implements Managed {

    private Map<String,RateLimiter> rateLimiterMap = new HashMap<String, RateLimiter>();;

    public RateLimiter getRateLimiter(String key) {
        return rateLimiterMap.get(key);
    }

    public void setRateLimiterMap(String key, RateLimiter rateLimiter) {
        this.rateLimiterMap.put(key,rateLimiter);
    }

    public void start() throws Exception {

    }

    public void stop() throws Exception {

    }
}
