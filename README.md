# ratelimiter
A dropwizard bundle for rate limiting APIs

### Supported Features
1. Rate limiting a group of APIs
2. Rate limiting individual APIs
3. Rate limiting individual APIs based on clients using X-Client-Id

### Integration with Dropwizard Service

YAML file containing application configuration
```yaml
ratelimiter:
  # JsonProperty of the HashMap present in RateLimitBundleConfiguration class
  groupKeyPermitsMap:
    groupkey1: 100
    groupkey2: 200
```

```java
public class MyConfiguration extends Configuration
{
    private RateLimitBundleConfiguration rateLimitBundleConfiguration;
    
    @JsonProperty("ratelimiter")
    public RateLimitBundleConfiguration getRateLimitBundleConfiguration() {
        return rateLimitBundleConfiguration;
    }
}
```

```java
public class MyApplication extends Application<MyConfiguration> 
{
  @Override
  public void initialize(Bootstrap<MyConfiguration> bootstrap){
      
     bootstrap.addBundle(new RateLimitBundle<MyConfiguration>(){
                 
         @Override
         protected RateLimitBundleConfiguration getRateLimitBundleConfiguration(MyConfiguration myConfiguration) {    
            return myConfiguration.getRateLimitBundleConfiguration();
         }
     });
  }
}
```

### Usage

#### 1. Rate limiting individual API

```java
@Path("/myresource")
public class MyResource{
    
    @GET
    @RateLimit(localPermits = 100)
    public Response getSomething(){
        
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
@Path("/myresource2")
public class MyResource{
    
    @POST
    @RateLimit(permitsGroupKey = "groupkey1")
    public Response postSomething(){
        
    }
}
```

getSomething() + postSomething() together are allowed 100qps

#### 3. Rate limiting individual API based on client distribution

```java
@Path("/myresource1")
public class MyResource{
    
    @GET
    @RateLimit(rateParam = @RateParam(clients = {@ClientParam(name = "client1", percent = 20),@ClientParam(name = 
    "client2", percent = 80)}),localPermits = 1000)
    public Response getSomething(){
        
    }
}
```
client1 would have 200qps rate limit
client2 would have 8000qps rate limit
Based on the X-Client-Id header, the permits would be granted.