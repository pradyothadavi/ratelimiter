package in.adavi.pradyot.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public class RateLimitBundleConfiguration {

    private Map<String,Double> globalKeyPermitsMap = new HashMap<String, Double>();

    public Map<String, Double> getGlobalKeyPermitsMap() {
        return globalKeyPermitsMap;
    }

    public void setGlobalKeyPermitsMap(Map<String, Double> globalKeyPermitsMap) {
        this.globalKeyPermitsMap = globalKeyPermitsMap;
    }

    public Double getGlobalPermits(String key) {
        return globalKeyPermitsMap.get(key);
    }

    public void setGlobalPermits(String key, Double permits) {
        globalKeyPermitsMap.put(key, permits);
    }

    public boolean containsKey(String key) {
        return globalKeyPermitsMap.containsKey(key);
    }
}
