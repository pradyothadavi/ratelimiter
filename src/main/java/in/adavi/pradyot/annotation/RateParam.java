package in.adavi.pradyot.annotation;

import in.adavi.pradyot.core.RateLimitKey;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public @interface RateParam {

    RateLimitKey key();

    String[] value();
}
