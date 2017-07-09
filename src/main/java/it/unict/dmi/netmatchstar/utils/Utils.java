package it.unict.dmi.netmatchstar.utils;

import java.math.BigDecimal;

/**
 * Created by fabior on 09/07/17.
 */
public class Utils {

    public static void printHeapMemoryInfo() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        System.out.print("Java current heap space is " + totalMemory + "/" + maxMemory);
        BigDecimal totalMemoryMb =
                new BigDecimal(totalMemory / Math.pow(1024, 2)).setScale(2, BigDecimal.ROUND_CEILING);
        BigDecimal maxMemoryMb =
                new BigDecimal(maxMemory / Math.pow(1024, 2)).setScale(2, BigDecimal.ROUND_CEILING);
        System.out.println(" (" + totalMemoryMb + " MB/" + maxMemoryMb + " MB)");
    }
}
