package com.github.pradyothadavi.core.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public class RateLimitBundleConfiguration {

    private Map<String,Double> groupLimits = new HashMap<String, Double>();

    private Map<String,Double> namedLimits = new HashMap<String, Double>();

    private Map<String, HeaderValueLimitMap> namedHeaderLimits = new HashMap<String, HeaderValueLimitMap>();

    public Map<String, Double> getGroupLimits() {
        return groupLimits;
    }

    public void setGroupLimits(Map<String, Double> groupLimits) {
        this.groupLimits = groupLimits;
    }

    public Map<String, Double> getNamedLimits() {
        return namedLimits;
    }

    public void setNamedLimits(Map<String, Double> namedLimits) {
        this.namedLimits = namedLimits;
    }

    public void setNamedHeaderLimits(Map<String, HeaderValueLimitMap> namedHeaderLimits){
        this.namedHeaderLimits = namedHeaderLimits;
    }

    public Map<String, HeaderValueLimitMap> getNamedHeaderLimits() {
        return namedHeaderLimits;
    }

    public Double getGroupLimit(String group){
        return this.groupLimits.get(group);
    }

    public Double getNamedLimit(String name){
        return this.namedLimits.get(name);
    }

    @Override
    public String toString() {
        return "RateLimitBundleConfiguration{" + "groupLimits=" + groupLimits + ", namedLimits=" + namedLimits + '}';
    }
}
