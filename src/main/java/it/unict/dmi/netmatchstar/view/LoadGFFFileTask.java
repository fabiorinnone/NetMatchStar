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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import it.unict.dmi.netmatchstar.utils.Common;
import it.unict.dmi.netmatchstar.utils.NetworkUtils;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class LoadGFFFileTask extends AbstractTask {
	
	private static boolean completedSuccessfully;
	
	private CySwingAppAdapter adapter;
	private File file;
	//private String fileName;
	
	private String fileName;
	private HashMap<Integer,CyNode> nodesMap;
	private ArrayList<String> nodeAttributes;
	
	private BufferedReader reader = null;
	
	private TaskMonitor taskMonitor;
	private boolean interrupted;
	
	int edgeCount;
	
	public LoadGFFFileTask(CySwingAppAdapter adapter, File file) {
		this.adapter = adapter;
		this.file = file;
		//this.fileName = fileName;
		
		fileName = file.getAbsolutePath();
		nodesMap = new HashMap<Integer,CyNode>();
		nodeAttributes = new ArrayList<String>();
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		taskMonitor = tm;
		
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		
		System.out.println("Loading GFF Network...");
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Loading GFF Network...");
		
		loadNetwork();
		
		if (interrupted) 
			return;
	}
	
	private void loadNetwork() throws IOException {
		CyNetwork network = NetworkUtils.createNetwork(adapter, fileName,
				Common.NODE_LABEL_ATTR, Common.EDGE_LABEL_ATTR);
		
		NetworkUtils.addNetwork(adapter, network);
		
		reader = new BufferedReader(new FileReader(fileName));
		reader.readLine(); //network name (but we use file name)
		reader.readLine(); //node number
		
		String line = "";
		while ((line = reader.readLine()).split(" ").length != 2 ) {
			String nodeAttr = line;
			nodeAttributes.add(nodeAttr);
		}
		nodeAttributes.remove(nodeAttributes.size()-1);
		addNodes(network);
		
		edgeCount = 0;
		addEdge(network, line);
			
		while((line = reader.readLine()) != null)
			addEdge(network, line);
		
		reader.close();
		
		VisualStyle vs = adapter.getVisualMappingManager().getDefaultVisualStyle();
		NetworkUtils.setVisualMappingFunction(adapter, vs, Common.NODE_LABEL_ATTR, null);
		
		NetworkUtils.addNetworkView(adapter, network);
	}
	
	private void addNodes(CyNetwork network) {
		CyTable nodeTable = network.getDefaultNodeTable();
		
		Iterator<String> nodeAttributesIterator = nodeAttributes.iterator();
		int nodeCount = 0;
		while(nodeAttributesIterator.hasNext()) {
			String nodeAttribute = nodeAttributesIterator.next();
			CyNode node = network.addNode();
			nodeTable.getRow(node.getSUID()).set(Common.NODE_LABEL_ATTR, nodeAttribute);
			if (nodeTable.getColumn(Common.NODE_ID_ATTR) != null)
				nodeTable.getRow(node.getSUID()).set(Common.NODE_ID_ATTR, "n"+ (nodeCount+1));
			if (nodeTable.getColumn(Common.NODE_NAME_ATTR) != null)
				nodeTable.getRow(node.getSUID()).set(Common.NODE_NAME_ATTR, "n"+ (nodeCount+1));
			
			nodesMap.put(nodeCount++, node);
		}
	}
	
	private void addEdge(CyNetwork network, String line) {
		String[] split = line.trim().split(" ");
		int sourceId = Integer.parseInt(split[0]);
		int targetId = Integer.parseInt(split[1]);
		CyNode source = nodesMap.get(sourceId);
		CyNode target = nodesMap.get(targetId);
		edgeCount++;
		CyEdge edge = network.addEdge(source, target, true);
		CyTable edgeTable = network.getDefaultEdgeTable();
		edgeTable.getRow(edge.getSUID()).set(Common.EDGE_LABEL_ATTR, "?");
		if (edgeTable.getColumn(Common.EDGE_ID_ATTR) != null)
			edgeTable.getRow(edge.getSUID()).set(
					Common.EDGE_ID_ATTR, "n" + (sourceId+1) + " (e" + (edgeCount+1) + ") n" + (targetId+1));
		if (edgeTable.getColumn(Common.EDGE_NAME_ATTR) != null)
			edgeTable.getRow(edge.getSUID()).set(
					Common.EDGE_NAME_ATTR, "n" + (sourceId+1) + " (e" + (edgeCount+1) + ") n" + (targetId+1));
		if (edgeTable.getColumn(Common.EDGE_INTER_ATTR) != null)
			edgeTable.getRow(edge.getSUID()).set(
					Common.EDGE_INTER_ATTR, "e" + (edgeCount+1));
	}
	
	public static boolean isCompletedSuccessfully() {
		return completedSuccessfully;
	}
    
    public String getTitle() {
        return "NetMatch*";
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
