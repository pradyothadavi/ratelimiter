package in.adavi.pradyot.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public class RateLimitBundleConfiguration {

    private Map<String,Double> groupKeyPermitsMap = new HashMap<String, Double>();

    public Map<String, Double> getGroupKeyPermitsMap() {
        return groupKeyPermitsMap;
    }

    public void setGroupKeyPermitsMap(Map<String, Double> groupKeyPermitsMap) {
        this.groupKeyPermitsMap = groupKeyPermitsMap;
    }

    public Double getGroupKeyPermits(String key) {
        return groupKeyPermitsMap.get(key);
    }

    public void setGroupKeyPermits(String key, Double permits) {
        groupKeyPermitsMap.put(key, permits);
    }

    public boolean containsKey(String key) {
        return groupKeyPermitsMap.containsKey(key);
    }
}
