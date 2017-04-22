# ratelimiter
A dropwizard bundle for rate limiting APIs

### Integration with Dropwizard Service

```java
public class MyApplication extends Application<MyConfiguration> 
{
  @Override
  public void initialize(Bootstrap<MyConfiguration> bootstrap){
    bootstrap.addBundle(new RateLimitBundle<MyConfiguration>()
    {
      @Override
      protected RateLimitBundleConfiguration getRateLimitBundleConfiguration(
                    MyConfiguration myConfiguration) {
        return myConfiguration.getRateLimitBundleConfiguration;
      }
    });
  }
}
```

```java
@Path("/mypath")
public class MyResource{
  
  @GET
  @RateLimit(permits = 10, timeUnit = TimeUnit.MINUTES)
  public Response fetchSomething(){
  
  }
}
```
