package com.github.pradyothadavi.app.web;

import com.github.pradyothadavi.annotation.HeaderValue;
import com.github.pradyothadavi.annotation.RateLimitByHeader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by pradyot.ha on 24/04/17.
 */
@Path("/ratelimitbyheader")
public class RateLimitByHeaderDemoResource {

    @GET
    @RateLimitByHeader(header = "X-Client-Id", rateLimits = {@HeaderValue(value = "client1", nameLimit = "client1.ratelimit"),@HeaderValue(value = "client2", ratePerSecond = 20)})
    public Response getSomething(){
        return Response.ok().build();
    }
}
