package com.github.pradyothadavi.core;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;

/**
 * Created by pradyot.ha on 24/04/17.
 */
public class RateLimitKey {

    private RateLimitAttribute rateLimitAttribute;
    private String attributeValue;

    public RateLimitAttribute getRateLimitAttribute() {
        return rateLimitAttribute;
    }

    public void setRateLimitAttribute(RateLimitAttribute rateLimitAttribute) {
        this.rateLimitAttribute = rateLimitAttribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String computeKey(ResourceInfo resourceInfo,ContainerRequestContext containerRequestContext){
        String key = null;
        switch (rateLimitAttribute){
            case RPS:
                key = resourceInfo.getResourceMethod().getName();
                break;
            case NAMED:
            case GROUP:
                key = resourceInfo.getResourceMethod().getName()+Constant.COLON+attributeValue;
                break;
            case HEADER:
                String headerValue = containerRequestContext.getHeaderString(attributeValue);
                key = resourceInfo.getResourceMethod().getName()+Constant.COLON+headerValue;
                break;
        }
        return key;
    }

    @Override
    public String toString() {
        return "RateLimitKey{" + "rateLimitAttribute=" + rateLimitAttribute + ", attributeValue='" + attributeValue + '\'' + '}';
    }
}
