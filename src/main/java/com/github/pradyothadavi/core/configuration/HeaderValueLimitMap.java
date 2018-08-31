package com.github.pradyothadavi.core.configuration;

import java.util.Map;

public class HeaderValueLimitMap {
    String header;
    Map<String, Double> limits;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Map<String, Double> getLimits() {
        return limits;
    }

    public void setLimits(Map<String, Double> limits) {
        this.limits = limits;
    }
}
