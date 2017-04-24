package com.github.pradyothadavi.app.web;

import com.github.pradyothadavi.annotation.RateLimit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by pradyot.ha on 24/04/17.
 */
@Path("/ratelimit")
public class RateLimitDemoResource {

    @GET
    @RateLimit(ratePerSecond = 3)
    public Response getSomething(){
        return Response.ok().build();
    }
}
