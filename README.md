# ratelimiter
A dropwizard bundle for rate limiting APIs

### Supported Features
1. Rate limiting a group of APIs
2. Rate limiting individual APIs

### Integration with Dropwizard Service

YAML file containing application configuration
```yaml
ratelimiter:
  # JsonProperty of the HashMap present in RateLimitBundleConfiguration class
  groupKeyPermitsMap:
    groupkey1: 100
    groupkey2: 200
    test.key: 2000
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
