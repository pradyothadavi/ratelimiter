package com.github.pradyothadavi.core;

import com.sun.jersey.spi.container.ContainerRequest;

import java.lang.reflect.Method;

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

    public String computeKey(Method method, ContainerRequest containerRequest){
        String key = null;
        switch (rateLimitAttribute){
            case RPS:
                key = method.getName();
                break;
            case NAMED:
            case GROUP:
                key = method.getName()+Constant.COLON+attributeValue;
                break;
            case HEADER:
                String headerValue = containerRequest.getHeaderValue(attributeValue);
                key = method.getName()+Constant.COLON+headerValue;
                break;
        }
        return key;
    }

    @Override
    public String toString() {
        return "RateLimitKey{" + "rateLimitAttribute=" + rateLimitAttribute + ", attributeValue='" + attributeValue + '\'' + '}';
    }

}
