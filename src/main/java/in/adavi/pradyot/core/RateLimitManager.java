package in.adavi.pradyot.core;

import com.google.common.util.concurrent.RateLimiter;
import in.adavi.pradyot.annotation.ClientParam;
import io.dropwizard.lifecycle.Managed;

import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pradyot.ha on 20/04/17.
 */
@Provider
public class RateLimitManager implements Managed {

    private Map<String,RateLimiter> rateLimiterMap = new HashMap<String, RateLimiter>();
    private Map<String,List<ClientParam>> groupKeyToClientsMap = new HashMap<String, List<ClientParam>>();
    private RateLimitBundleConfiguration rateLimitBundleConfiguration;

    public RateLimitManager(RateLimitBundleConfiguration rateLimitBundleConfiguration) {
        this.rateLimitBundleConfiguration = rateLimitBundleConfiguration;
    }

    public Map<String, RateLimiter> getRateLimiterMap() {
        return rateLimiterMap;
    }

    public RateLimiter getRateLimiter(String key) {
        return rateLimiterMap.get(key);
    }

    public void setRateLimiter(String key, RateLimiter rateLimiter) {
        this.rateLimiterMap.put(key,rateLimiter);
    }

    public Map<String, List<ClientParam>> getGroupKeyToClientsMap() {
        return groupKeyToClientsMap;
    }

    public List<ClientParam> getClients(String globalRateKey){
        return this.groupKeyToClientsMap.get(globalRateKey);
    }

    public void setClients(String globalRateKey,List<ClientParam> clientParams){
        if(this.groupKeyToClientsMap.containsKey(globalRateKey))
        {
            List<ClientParam> existingClientParams = this.getGroupKeyToClientsMap().get(globalRateKey);
            existingClientParams.addAll(clientParams);
            groupKeyToClientsMap.put(globalRateKey, existingClientParams);
        } else {
            this.groupKeyToClientsMap.put(globalRateKey, clientParams);
        }
    }

    public void start() throws Exception {
        if(!groupKeyToClientsMap.isEmpty()){
            createRateLimiters();
        }
    }

    private void createRateLimiters() {
        for (Map.Entry<String,List<ClientParam>> entry : groupKeyToClientsMap.entrySet()) {
            Double globalPermits = rateLimitBundleConfiguration.getGroupKeyPermits(entry.getKey());
            for (ClientParam clientParam : entry.getValue()) {
                Double permits = (globalPermits*clientParam.percent())/100;
                String rateLimiterKey = entry.getKey()+":"+ clientParam.name();
                RateLimiter rateLimiter = RateLimiter.create(permits);
                setRateLimiter(rateLimiterKey,rateLimiter);
            }
        }
    }

    public void stop() throws Exception {

    }
}
