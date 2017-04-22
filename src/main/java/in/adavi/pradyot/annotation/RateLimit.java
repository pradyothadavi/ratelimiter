package in.adavi.pradyot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by pradyot.ha on 20/04/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface RateLimit {

    RateParam rateParam() default @RateParam();

    double localPermits() default 0;

    String permitsGlobalKey() default "";

    long warmUpPeriod() default 0;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
