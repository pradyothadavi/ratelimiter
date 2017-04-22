package in.adavi.pradyot.annotation;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public @interface RateParam {

    ClientParam[] clients() default @ClientParam(name = "", percent = 0);
}
