package in.adavi.pradyot.core;

import in.adavi.pradyot.annotation.ClientParam;
import in.adavi.pradyot.annotation.RateParam;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public class Util {

    public static boolean isEmpty(String value){
        if("".equals(value) || null == value)
            return true;
        return false;
    }

    public static boolean isNotEmpty(String value){
        return !isEmpty(value);
    }

    public static boolean isZero(Double value){
        Double zero = new Double(0.0);
        if(0 == zero.compareTo(value))
            return true;
        return false;
    }

    public static boolean notZero(Double value){
        return !isZero(value);
    }

    public static boolean hasDefaultClient(RateParam rateParam){
        ClientParam[] clientParams = rateParam.clients();
        if(1 == clientParams.length && isEmpty(clientParams[0].name()))
            return true;
        return false;
    }

    public static boolean hasClientRateParam(RateParam rateParam){
        return !hasDefaultClient(rateParam);
    }
}
