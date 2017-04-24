package com.github.pradyothadavi;

import com.github.pradyothadavi.app.RateLimitApp;
import com.github.pradyothadavi.app.web.RateLimitConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * Created by pradyot.ha on 24/04/17.
 */
public class RateLimitBundleTest {

    @ClassRule
    public static final DropwizardAppRule<RateLimitConfiguration> RULE =
        new DropwizardAppRule<RateLimitConfiguration>(RateLimitApp.class, ResourceHelpers.resourceFilePath("config.yml"));

    @Test
    public void test(){

        Client client = ClientBuilder.newBuilder().build();

        Response response = client.target(String.format("http://localhost:%d/admin/tasks/rateLimiter", RULE.getAdminPort()))
                                .request()
                                .post(Entity.json(null));

        System.out.print(response.readEntity(String.class));
    }
}
