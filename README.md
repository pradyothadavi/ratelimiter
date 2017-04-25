# ratelimiter
A dropwizard bundle for rate limiting APIs

### Supported Features
1. Rate limiting a group of APIs
2. Rate limiting individual APIs
3. Rate limiting individual APIs based on clients using X-Client-Id

### Integration with Dropwizard Service

YAML file containing application configuration
```yaml
rateLimiter:
  groupLimits:
    group: 15
  namedLimits:
    client1.ratelimit: 5
```

```java
public class RateLimitApp extends Application<RateLimitConfiguration> {

    @Override
    public void initialize(Bootstrap<RateLimitConfiguration> bootstrap) {
        super.initialize(bootstrap);

        bootstrap.addBundle(new RateLimitBundle<RateLimitConfiguration>() {
            @Override
            protected RateLimitBundleConfiguration getRateLimitBundleConfiguration(RateLimitConfiguration configuration) {
                return configuration.getRateLimitBundleConfiguration();
            }
        });
    }

    public void run(RateLimitConfiguration rateLimitConfiguration, Environment environment) throws Exception {
        environment.jersey().register(new RateLimitDemoResource());
        environment.jersey().register(new RateLimitByGroupDemoResource());
        environment.jersey().register(new RateLimitByHeaderDemoResource());
    }
}
```

```java
public class RateLimitConfiguration extends Configuration {

    private RateLimitBundleConfiguration rateLimitBundleConfiguration;

    @JsonProperty("rateLimiter")
    public RateLimitBundleConfiguration getRateLimitBundleConfiguration() {
        return rateLimitBundleConfiguration;
    }

    public void setRateLimitBundleConfiguration(RateLimitBundleConfiguration rateLimitBundleConfiguration) {
        this.rateLimitBundleConfiguration = rateLimitBundleConfiguration;
    }
}
```

### Usage

#### 1. Rate limiting individual API

```java
@Path("/ratelimit")
public class RateLimitDemoResource {

    @GET
    @RateLimit(ratePerSecond = 3)
    public Response getSomething(){
        return Response.ok().build();
    }
}
```

#### 2. Rate limiting group of APIs

```java
@Path("/myresource1")
public class MyResource{
    
    @GET
    @RateLimit(permitsGroupKey = "groupkey1")
    public Response getSomething(){
        
    }
}
```

```java
@Path("/ratelimitbygroup")
public class RateLimitByGroupDemoResource {

    @GET
    @RateLimitByGroup("group")
    public Response getSomething(){
        return Response.ok().build();
    }
}
```

```java
@Path("/ratelimitbygroup2")
public class RateLimitByGroupDemoResource2 {

    @GET
    @RateLimitByGroup("group")
    public Response getSomething2(){
        return Response.ok().build();
    }
}
```
getSomething() and getSomething2() put together has rate limit of 15.
#### 3. Rate limiting individual API based on client distribution

```java
@Path("/ratelimitbyheader")
public class RateLimitByHeaderDemoResource {

    @GET
    @RateLimitByHeader(header = "X-Client-Id", rateLimits = {@HeaderValue(value = "client1", nameLimit = "client1.ratelimit"),@HeaderValue(value = "client2", ratePerSecond = 20)})
    public Response getSomething(){
        return Response.ok().build();
    }
}
```

#### Display rate limiters
http://host:admin_port/admin/tasks/rateLimiter