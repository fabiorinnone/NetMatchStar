package it.unict.dmi.netmatchstar.utils.layout;

import it.unict.dmi.netmatchstar.utils.Common;
import it.unict.dmi.netmatchstar.utils.Utils;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * Created by fabior on 19/09/17.
 */
public class NetMatchStyleTask extends AbstractTask {

    private static boolean completedSuccessfully;

    private CySwingAppAdapter adapter;

    private TaskMonitor taskMonitor;
    private boolean interrupted;

    public NetMatchStyleTask(CySwingAppAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run(TaskMonitor tm) throws Exception {
        taskMonitor = tm;

        if (taskMonitor == null) {
            throw new IllegalStateException("Task Monitor is not set.");
        }

        taskMonitor.setProgress(-1.0);
        taskMonitor.setStatusMessage("Creating NetMatch* Layout...");

        //Thread.sleep(100);
        Utils.createNetMatchStarStyle(adapter);

        if (interrupted)
            return;
    }

    public static boolean isCompletedSuccessfully() {
        return completedSuccessfully;
    }

    public String getTitle() {
        return Common.APP_NAME;
    }

    public void halt() {
        interrupted = true;
    }

    public void setTaskMonitor(TaskMonitor tm) throws IllegalThreadStateException {
        if(taskMonitor != null)
            throw new IllegalStateException("Task Monitor is already set.");
        taskMonitor = tm;
    }

    @Override
    public void cancel() {
        //this.interrupted = true;
    }
}
