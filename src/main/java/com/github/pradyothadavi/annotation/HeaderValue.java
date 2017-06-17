package com.github.pradyothadavi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Created by pradyot.ha on 24/04/17.
 */

/**
 * @see RateLimitByHeader
 * <p>
 * This annotation facilitates rate limiting of API based on header information.
 * Using this annotation, you will be able to specify the parameters for rate limiting for the value of the header key
 * mentioned in {@link RateLimitByHeader#header()}
 * </p>
 * The annotation supports the following ways of specifying
 * rate limit
 * <table>
 *   <caption>Rate Limiter</caption>
 *   <tr>
 *     <th>Parameter</th>
 *     <th>Comment</th>
 *   </tr>
 *   <tr>
 *     <td>RatePerSecond</td>
 *     <td>Numeric Value</td>
 *   </tr>
 *   <tr>
 *     <td>Name Limit</td>
 *     <td>A named alias for rate per second which you can read from application configuration.</td>
 *   </tr>
 * </table>
 * <p>
 *   Above rate limits are applied for the value of the {@link RateLimitByHeader#header()} which is specifies in
 * {@link #value()}
 * </p>
 *
 * <p>
 *   * Example : Suppose an API is being accessed by two clients, where the clients are identified based on the header
 * X-Client-Id. Further you are not sure what value to rate limit for client2. Then you can annotate your API as follows
 * </p>
 * <code>
 *   Path("/ratelimitbyheader")
 *   public RateLimitByHeaderDemoResource{
 *
 *     GET
 *     RateLimitByHeader(header = "X-Client-Id", rateLimts = {
 *                                                  HeaderValue(value = "client1", ratePerSecond = 100),
 *                                                  HeaderValue(value = "client2", nameLimit = "client2.rps")})
 *     public Response getSomething(){
 *       ...
 *     }
 *   }
 * </code>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface HeaderValue {

    /**
     * The field captures the value of the header mentioned in {@link RateLimitByHeader#header()}. Specified rate limit
     * would be applied against this value.
     * @return header value as String
     */
    String value();

    /**
     * The field captures the rate limit on a per second to be applied on the value captured in {@link #value()}. This
     * must be a non-negative field.
     * @return rate limit as double.
     */
    double ratePerSecond() default 0;

    /**
     * The field captures a named alias for rate limit on a per second. The value of this field represents the key
     * corresponding to which a rate limit is specified. This field allows you to change the rate limit for an API
     * which redeploying the application. The value corresponding to named alias must be present in the application
     * configuration file.
     * @return named alias for rate limit
     */
    String nameLimit() default "";
}
