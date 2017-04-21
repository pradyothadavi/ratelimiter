package in.adavi.pradyot.core;

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
}
