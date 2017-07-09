package it.unict.dmi.netmatchstar.utils;

import java.util.TimerTask;

/**
 * Created by fabior on 09/07/17.
 */
public class PrintHeapInfo extends TimerTask {

    @Override
    public void run() {
        Utils.printHeapMemoryInfo();
    }
}
