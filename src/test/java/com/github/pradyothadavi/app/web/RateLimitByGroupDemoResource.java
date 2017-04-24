package com.github.pradyothadavi.app.web;

import com.github.pradyothadavi.annotation.RateLimitByGroup;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by pradyot.ha on 24/04/17.
 */
@Path("/ratelimitbygroup")
public class RateLimitByGroupDemoResource {

    @GET
    @RateLimitByGroup("group")
    public Response getSomething(){
        return Response.ok().build();
    }
}
