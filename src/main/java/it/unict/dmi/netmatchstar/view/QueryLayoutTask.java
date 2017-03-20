/*
 * Copyright (c) 2015, Rosalba Giugno.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the University of Catania nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ROSALBA GIUGNO ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ROSALBA GIUGNO BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package it.unict.dmi.netmatchstar.view;

import it.unict.dmi.netmatchstar.utils.Common;
import it.unict.dmi.netmatchstar.utils.NetworkUtils;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class QueryLayoutTask extends AbstractTask {
	
	private static boolean completedSuccessfully;
	
	private CySwingAppAdapter adapter;
	private VisualStyle vs;
	private CyNetworkView netView;
	
	private TaskMonitor taskMonitor;
	private boolean interrupted;

	private VisualMappingManager manager;
	
	public QueryLayoutTask(CySwingAppAdapter adapter, VisualStyle vs, CyNetworkView netView) {
		this.adapter = adapter;
		this.vs = vs;
		this.netView = netView;
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		taskMonitor = tm;
		
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Setting Query Layout...");
		
		manager = adapter.getVisualMappingManager();
		
		Thread.sleep(100); 
		NetworkUtils.configureQueryVisualStyle(vs, adapter);
		
		if (interrupted)
			return;

		vs.apply(netView);
		manager.setVisualStyle(vs, netView);

		//netView.fitContent();
		netView.updateView();
		
		CyLayoutAlgorithmManager clam = adapter.getCyLayoutAlgorithmManager();
		CyLayoutAlgorithm alg = clam.getLayout("kamada-kawai");
		TaskIterator ti = alg.createTaskIterator(netView, alg.getDefaultLayoutContext(),
				CyLayoutAlgorithm.ALL_NODE_VIEWS,null);
		
		super.insertTasksAfterCurrentTask(ti);
		
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
