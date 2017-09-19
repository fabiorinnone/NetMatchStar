package it.unict.dmi.netmatchstar.utils;

/**
 * Created by fabior on 19/09/17.
 */
public class Utils {

    public static boolean isApproximatePath(String s) {
        return 	s.startsWith(Common.APPROX_GT) ||  s.startsWith(Common.APPROX_GE) ||
                s.startsWith(Common.APPROX_EQ) ||  s.startsWith(Common.APPROX_LT) ||
                s.startsWith(Common.APPROX_LE);
    }

    public static String isNumber(String s) {
        if(s.startsWith(Common.GE))
            return Common.GE;
        else if(s.startsWith(Common.GT))
            return Common.GT;
        else if(s.startsWith(Common.LE))
            return Common.LE;
        else if(s.startsWith(Common.LT))
            return Common.LT;
        return Common.UNDEFINED;
    }

    public static String getNumber(String s) {
        int index = s.indexOf("=");
        if(s.charAt(1) == '=')
            return s.substring(2);
        else
            return s.substring(1);
    }
}
