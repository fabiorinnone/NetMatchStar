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
package it.unict.dmi.netmatchstar.algorithm.metrics;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import it.unict.dmi.netmatchstar.CyActivator;
import it.unict.dmi.netmatchstar.algorithm.ApproxEdgeComparator;
import it.unict.dmi.netmatchstar.algorithm.ApproxNodeComparator;
import it.unict.dmi.netmatchstar.algorithm.ExactEdgeComparator;
import it.unict.dmi.netmatchstar.algorithm.ExactNodeComparator;
import it.unict.dmi.netmatchstar.algorithm.significance.RandomGenerator;
import it.unict.dmi.netmatchstar.graph.Graph;
import it.unict.dmi.netmatchstar.graph.GraphLoader;
import it.unict.dmi.netmatchstar.utils.Common;
import it.unict.dmi.netmatchstar.view.WestPanel;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * @author Fabio Rinnone
 *
 */
@SuppressWarnings("rawtypes")
public class MetricsTask extends AbstractTask {
	private boolean m_direct;
	
	private JPanel frame;
	
	private CyNetwork target;
	private CyNetwork query;
	private String qea, qna;
	private ArrayList tea, tna;
	
	private GraphLoader qLoader;
	private boolean isApproximate;
	private boolean isUnlabeled;
	private Vector approxPaths;
	
	private TaskMonitor taskMonitor;
	private boolean interrupted;
	private CyActivator activator;
	private int N;
	private JTextArea log;
	
	private static boolean completedSuccessfully;
	
	private double dbAverageDegree;
	private double dbAverageClusteringCoefficient;
	private double dbAssortativity;
	
	private double erAverageDegree;
	private double erAverageClusteringCoefficient;
	private double erAssortativity;

	private double wsAverageDegree;
	private double wsAverageClusteringCoefficient;
	private double wsAssortativity;

	private double baAverageDegree;
	private double baAverageClusteringCoefficient;
	private double baAssortativity;

	private double gmAverageDegree;
	private double gmAverageClusteringCoefficient;
	private double gmAssortativity;

	private double dmAverageDegree;
	private double dmAverageClusteringCoefficient;
	private double dmAssortativity;

	private double ffmAverageDegree;
	private double ffmAverageClusteringCoefficient;
	private double ffmAssortativity;

	private double smAverageDegree;
	private double smAverageClusteringCoefficient;
	private double smAssortativity;

	private int shufflingQ;
	private boolean labelShuffling;
	private double wsProb;
	private int baInitNumNodes;
	private int gmDim;
	private int dmInitNumNodes;
	private double dmInitProbEdges;
	private int ffmNumAmb;
	
	public MetricsTask(int shufflingQ, boolean labShuffling, double wsProb, int baInitNumNodes, 
			int gmDim, int ffmNumAmb, int dmInitNumNodes, double dmInitProbEdges, 
			boolean direct, CyNetwork t, CyNetwork q, ArrayList tel, 
			ArrayList tnl, String qeaa, String qnaa, JPanel frame2, CyActivator activator) {
		this.shufflingQ = shufflingQ;
		this.labelShuffling = labShuffling;
		this.wsProb = wsProb;
		this.baInitNumNodes = baInitNumNodes;
		this.gmDim = gmDim;
		this.dmInitNumNodes = dmInitNumNodes;
		this.dmInitProbEdges = dmInitProbEdges;
		this.ffmNumAmb = ffmNumAmb;
		
		m_direct = direct;
		
		target = t;
		query = q;
		tea = tel;
		tna = tnl;
		frame = frame2;
		qea = qeaa;
		qna = qnaa;
		
		qLoader = null;
		isApproximate = false;
		approxPaths = null;
		
		this.activator = activator;
	}
	
	public MetricsTask(int shufflingQ, boolean labShuffling, double wsProb, int baInitNumNodes, 
			int gmDim, int ffmNumAmb, int dmInitNumNodes, double dmInitProbEdges, 
			boolean direct, CyNetwork t, CyNetwork q, ArrayList tel, 
			ArrayList tnl, String qeaa, String qnaa, boolean iqa, boolean iqu, JPanel frame2,
			CyActivator activator) {
		this.shufflingQ = shufflingQ;
		this.labelShuffling = labShuffling;
		this.wsProb = wsProb;
		this.baInitNumNodes = baInitNumNodes;
		this.gmDim = gmDim;
		this.dmInitNumNodes = dmInitNumNodes;
		this.dmInitProbEdges = dmInitProbEdges;
		this.ffmNumAmb = ffmNumAmb;
		
		m_direct = direct;
		
		target = t;
		query = q;
		tea = tel;
		tna = tnl;
		frame = frame2;
		qea = qeaa;
		qna = qnaa;
		
		isApproximate = iqa;
		isUnlabeled = iqu;
		
		//approxPaths = ap;
		approxPaths = new Vector<String>();
		
		this.activator = activator;
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		taskMonitor = tm;
		
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		
		try {
			System.out.println("Create Network Loader (Step 1 of 5)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Network Loader (Step 1 of 5)");
			GraphLoader dbLoader = loadGraphFromNetwork(target, tea, tna);
			if(interrupted)
				return;
			System.out.println("Create Network Graph Data (Step 2 of 5)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Network Graph Data (Step 2 of 5)");
			Graph db = new Graph(dbLoader, Common.DIRECTED);
			if(interrupted)
				return;
			System.out.println("Create Query Loader (Step 3 of 5)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Query Loader (Step 3 of 5)");
			//if (!isApproximate) 
				qLoader = loadGraphFromNetwork(query, qea, qna);
			if(interrupted)
				return;
			System.out.println("Create Query Graph Data (Step 4 of 5)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Query Graph Data (Step 4 of 5)");
			Graph q = new Graph(qLoader, Common.DIRECTED);
			if(Common.LABELED) {
				q.setNodeComparator(new ExactNodeComparator());
				q.setEdgeComparator(new ExactEdgeComparator());
			}
			else {
				q.setNodeComparator(new ApproxNodeComparator());
				q.setEdgeComparator(new ApproxEdgeComparator());
			}
			if(interrupted)
				return;
			
			System.out.println("Computing metrics... (Step 5 of 5)");
			taskMonitor.setProgress(0.0);
			taskMonitor.setStatusMessage("Computing metrics... (Step 5 of 5)");
		
	      	if(interrupted)
	      		return;
	      	
	      	dbAverageDegree = Metrics.getAverageDegree(db);
	      	dbAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(db);
	      	dbAssortativity = Metrics.getAssortativity(db);
	      	
	      	taskMonitor.setProgress(1.0/8.0);
	      	
	      	Graph shufflNet = db;
	      	//Shuffling
	      	if(m_direct) {
	        	//System.out.println("Random shuffling directed graph " + i + "...");
	      		shufflNet.inOutDegreePreservingShuffling(shufflingQ * shufflNet.getEdgeCount(), true);
	        }
	        else {
	        	//System.out.println("Random shuffling undirected graph " + i + "...");
	        	shufflNet.inOutDegreePreservingShuffling(shufflingQ * shufflNet.getEdgeCount(), false);
	        }

	        if(labelShuffling) {
	        	shufflNet.nodeLabelShuffling();
	        	shufflNet.edgeLabelShuffling();
	        }

	        smAverageDegree = Metrics.getAverageDegree(shufflNet);
	        smAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(shufflNet);
	        smAssortativity = Metrics.getAssortativity(shufflNet);
	        
	        taskMonitor.setProgress(2.0/8.0);
	      	
	      	int numNodes = target.getNodeCount();
	        int numEdges = target.getEdgeCount();
	        RandomGenerator randomGenerator = new RandomGenerator(db.nodes(), numNodes, numEdges, m_direct);
	        
	      	//Erdos-Renyi
	        Graph erdosRenyiNet = randomGenerator.createErdosRenyi();
	        erAverageDegree = Metrics.getAverageDegree(erdosRenyiNet);
	        erAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(erdosRenyiNet);
	        erAssortativity = Metrics.getAssortativity(erdosRenyiNet);
	        
	        taskMonitor.setProgress(3.0/8.0);
	      	
	        //Watts-Strogatz
	        Graph wattsStrogatzNet = randomGenerator.createWattsStrogatz(wsProb);
	        wsAverageDegree = Metrics.getAverageDegree(wattsStrogatzNet);
	        wsAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(wattsStrogatzNet);
	        wsAssortativity = Metrics.getAssortativity(wattsStrogatzNet);
	        
	        taskMonitor.setProgress(4.0/8.0);
	      	
	        //Barabasi-Albert
	        Graph barabasiAlbertNet = randomGenerator.createAlbertBarabasi(baInitNumNodes);
	        baAverageDegree = Metrics.getAverageDegree(barabasiAlbertNet);
	        baAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(barabasiAlbertNet);
	        baAssortativity = Metrics.getAssortativity(barabasiAlbertNet);
	        
	        taskMonitor.setProgress(5.0/8.0);
	      	
	        //Geometric
	        Graph geometricNet = randomGenerator.createGeometric(gmDim);
	        gmAverageDegree = Metrics.getAverageDegree(geometricNet);
	        gmAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(geometricNet);
	        gmAssortativity = Metrics.getAssortativity(geometricNet);
	        
	        taskMonitor.setProgress(6.0/8.0);
	      	
	        //Duplication
	        Graph duplicationNet = randomGenerator.createDuplication(dmInitNumNodes, dmInitProbEdges);
	        dmAverageDegree = Metrics.getAverageDegree(duplicationNet);
	        dmAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(duplicationNet);
	        dmAssortativity = Metrics.getAssortativity(duplicationNet);
	        
	        taskMonitor.setProgress(7.0/8.0);
	      	
	        //Forest-Fire
	        Graph forestFireNet = randomGenerator.createForestFire(ffmNumAmb);
	        ffmAverageDegree = Metrics.getAverageDegree(forestFireNet);
	        ffmAverageClusteringCoefficient = Metrics.getAverageClusteringCoefficient(forestFireNet);
	        ffmAssortativity = Metrics.getAssortativity(forestFireNet);
	        
	        taskMonitor.setProgress(8.0/8.0);
	      	
	      	completedSuccessfully = true;
    	      
	        N = WestPanel.getRandomNets().getValue();
	        
	        log = WestPanel.getLog();
		    
	        if(isCompletedSuccessfully()) {
        		SwingUtilities.invokeLater(new Runnable() {
	          		public void run() {
	          			showMetricsResult();
	          		}
	          	});
	          	
	            if(interrupted)
	        		return;
	        }
	        
	      	System.out.println("Task completed");
	      	
	      	if(interrupted) 
	      		return;
		}
		catch (Exception e) {
			completedSuccessfully = false;
			System.out.println(e);
			throw new Exception("Please check data!", e);
		}
		catch (Error e) {
			completedSuccessfully = false;
			System.out.println(e);
			throw new Exception(Common.APP_NAME + " cancelled", e);
		}
		finally {
			//No op
		}
	}
	
	public GraphLoader loadGraphFromNetwork(CyNetwork network, ArrayList edgeAttr, ArrayList nodeAttr) throws Exception {   	
    	GraphLoader loader;
        Hashtable names = new Hashtable();
        int i, k;
        boolean any;
        i = k = 0;
        loader = new GraphLoader(activator, frame);
        int size = network.getNodeCount() + network.getEdgeCount();
        for (CyNode node : network.getNodeList()) {
			CyRow nodeRow = network.getRow(node);
			Class<?> rowType;
			ArrayList name1Attr = new ArrayList();
			for (int vi = 0; vi < nodeAttr.size(); vi++) {
				rowType = nodeRow.getTable().getColumn(nodeAttr.get(vi).toString()).getType();
				/*if (Collection.class.isAssignableFrom(type)) {
					Collection valueList = (Collection) row.get(nodeAttr.get(vi).toString(), type);			
					if (valueList != null) {
						for (Object value : valueList) {
							attributeValues.add(value);
						}
					}
				} 
				else {*/
				if (!Collection.class.isAssignableFrom(rowType)) {
					name1Attr.add(nodeRow.get(nodeAttr.get(vi).toString(), rowType));
				}
			}
			String name1 = node.getSUID().toString();
			if(!names.containsKey(name1)) {
				names.put(name1, new Integer(i++));
				name1Attr.add(0, name1 + Common.STD_EDGE);
				loader.insertNode(name1Attr, network.getSUID().intValue(), false);
			}
			taskMonitor.setProgress((++k * 100) / size);
			if(interrupted)
				return null;
        }
		//ArrayList name2Attr = new ArrayList();
    	//ArrayList edge2Attr = new ArrayList();
        for (CyEdge edge : network.getEdgeList()) {
        	CyRow edgeRow = network.getRow(edge);
        	ArrayList edge2Attr = new ArrayList();
        	CyNode source = edge.getSource();
        	CyNode dest = edge.getTarget();
        	String name1 = source.getSUID().toString();
        	String name2 = dest.getSUID().toString();
        	CyRow destRow = network.getRow(edge.getTarget());
        	Class<?> type;
        	for (int ai = 0; ai < edgeAttr.size(); ai++) {
        		type = edgeRow.getTable().getColumn(edgeAttr.get(ai).toString()).getType();
        		edge2Attr.add(edgeRow.get(edgeAttr.get(ai).toString(), type));
        	}	
        	ArrayList name2Attr = new ArrayList();
        	Class<?> type2;
        	for (int vi = 0; vi < nodeAttr.size(); vi++) {
        		type2 = destRow.getTable().getColumn(nodeAttr.get(vi).toString()).getType();
        		/*if (Collection.class.isAssignableFrom(type2)) {
        			Collection valueList = (Collection) destRow.get(nodeAttr.get(vi).toString(), type2);
        			if (valueList != null) {
        				for (Object value : valueList) {
        					name2Attr.add(value);
        				}
        			}
        		} 
        		else {*/
        		if (!Collection.class.isAssignableFrom(type2)) {
        			name2Attr.add(destRow.get(nodeAttr.get(vi).toString(), type2));
        		}
        		any = false;
        		if(name1.equals(name2)) {
        			name2 += Common.SELF_EDGE;
        			if(!names.containsKey(name2)) {
        				names.put(name2, new Integer(i++));
        				name2Attr.add(0, name2);
        				loader.insertNode(name2Attr, network.getSUID().intValue(), true);
        			}
        			any = true;
        		}
        		if(any) {
        			loader.insertEdge(((Integer)names.get(name1)), ((Integer)names.get(name2)), edge2Attr, true);
        			if(!Common.DIRECTED) {
        				loader.insertEdge(((Integer)names.get(name2)), ((Integer)names.get(name1)), edge2Attr, true);
        			}
        		}
        		else {
        			loader.insertEdge(((Integer)names.get(name1)), ((Integer)names.get(name2)), edge2Attr, false);
        			if(!Common.DIRECTED) {
        				loader.insertEdge(((Integer)names.get(name2)), ((Integer)names.get(name1)), edge2Attr, false);
        			}
        		}
        		taskMonitor.setProgress((++k * 100) / size);
        		if(interrupted)
        			return null;
        	}
        }
        return loader;
    }
    
    public GraphLoader loadGraphFromNetwork(CyNetwork network, String edgeAttr, String nodeAttr) throws Exception {
    	GraphLoader loader;
        Hashtable names = new Hashtable();
        int i, k;
        boolean any;
        i = k = 0;
        loader = new GraphLoader(activator, frame);
        int size = network.getNodeCount() + network.getEdgeCount();
        for (CyNode node : network.getNodeList()) {
			CyRow row = network.getRow(node);
			Class<?> type = row.getTable().getColumn(nodeAttr).getType();
			String name1 = node.getSUID().toString();
			String name1Attr = null;
	    	if (Collection.class.isAssignableFrom(type)) {
	    		name1Attr = row.get(nodeAttr, type).toString(); //TODO è una lista di attributi?
	    	}
	    	else {
				name1Attr = row.get(nodeAttr, type).toString();
			}
	    	if(!names.containsKey(name1)) {
	    		names.put(name1, new Integer(i++));
	    		loader.insertNode(name1Attr, network.getSUID().intValue(), false);
	    	}
	    	taskMonitor.setProgress((++k * 100) / size);
	    	if(interrupted)
	    		return null;
    	}
        for (CyEdge edge : network.getEdgeList()) {
			CyRow erow = network.getRow(edge);
			Class<?> type2 = erow.getTable().getColumn(edgeAttr).getType();
			String name1 = edge.getSUID().toString();
			String type = null;
			/*if (Collection.class.isAssignableFrom(type2)) {
				type = erow.get(edgeAttr, type2).toString(); //TODO è una lista di attributi?
      		}
			else {*/
			if (!Collection.class.isAssignableFrom(type2)) {
				type = erow.get(edgeAttr, type2).toString();
			}
			CyNode source = (CyNode)edge.getSource();
			CyNode dest = (CyNode)edge.getTarget();
			name1 = source.getSUID().toString();
			String name2 = dest.getSUID().toString();
			String name2Attr = null;
			CyRow row = network.getRow(dest);
			Class<?> typeDest = row.getTable().getColumn(nodeAttr).getType();
			/*if (Collection.class.isAssignableFrom(typeDest)) {
				name2Attr = row.get(nodeAttr, typeDest).toString(); 
  			}
			else {*/
			if (!Collection.class.isAssignableFrom(typeDest)) {
				name2Attr = row.get(nodeAttr, typeDest).toString();
			}	
			any = false;
			if(name1.equals(name2)) {
				name2 += Common.SELF_EDGE;
				name2Attr += Common.SELF_EDGE;		
				if(!names.containsKey(name2)) {
					names.put(name2, new Integer(i++));
					loader.insertNode(name2Attr, network.getSUID().intValue(), true);
				}
				any = true;
			}
  			if (!Common.isApproximatePath(type)) { //non è un cammino approssimato
				if(any) {
					loader.insertEdge(((Integer)names.get(name1)), ((Integer)names.get(name2)), type, true);
					if (!Common.DIRECTED) {
						loader.insertEdge(((Integer)names.get(name2)), ((Integer)names.get(name1)), type, true);
					}
	          	}
	          	else {
	          		loader.insertEdge(((Integer)names.get(name1)), ((Integer)names.get(name2)), type, false);
	          		if(!Common.DIRECTED) {
	          			loader.insertEdge(((Integer)names.get(name2)), ((Integer)names.get(name1)), type, false);
	          		}
	          	}
			}
			else { //è un cammino approssimato
				Integer index1 = (Integer)names.get(name1);
				Integer index2 = (Integer)names.get(name2);
				String approxPath = index1+","+index2+","+type;
				if (!approxPaths.contains(approxPath)) 
					approxPaths.add(index1+","+index2+","+type);
			}
			taskMonitor.setProgress((++k * 100) / size);
			if(interrupted)
				return null;
		}
		return loader;
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

	private void showMetricsResult() {
		log.append("Average degree in input network: " + dbAverageDegree + "\n");
		System.out.println("Average degree in input network: " + dbAverageDegree);
		log.append("Average clustering coefficient in input network: " + dbAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in input network: " + dbAverageClusteringCoefficient);
		log.append("Assortativity in input network: " + dbAssortativity + "\n");
		System.out.println("Assortativity in input network: " + dbAssortativity);

		log.append("Average degree in shuffled network: " + smAverageDegree + "\n");
		System.out.println("Average degree in shuffled network: " + smAverageDegree);
		log.append("Average clustering coefficient in shuffled network: " + smAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in shuffled network: " + smAverageClusteringCoefficient);
		log.append("Assortativity in shuffled network: " + smAssortativity + "\n");
		System.out.println("Assortativity in shuffled network: " + smAssortativity);

		log.append("Average degree in Erdos-Renyi network: " + erAverageDegree + "\n");
		System.out.println("Average degree in Erdos-Renyi network: " + erAverageDegree);
		log.append("Average clustering coefficient in Erdos-Renyi network: " + erAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in Erdos-Renyi network: " + erAverageClusteringCoefficient);
		log.append("Assortativity in Erdos-Renyi network: " + erAssortativity + "\n");
		System.out.println("Assortativity in Erdos-Renyi network: " + erAssortativity);

		log.append("Average degree in Watts-Stogatz network: " + wsAverageDegree + "\n");
		System.out.println("Average degree in Watts-Stogatz network: " + wsAverageDegree);
		log.append("Average clustering coefficient in Watts-Stogatz network: " + wsAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in Watts-Stogatz network: " + wsAverageClusteringCoefficient);
		log.append("Assortativity in Watts-Stogatz network: " + wsAssortativity + "\n");
		System.out.println("Assortativity in Watts-Stogatz network: " + wsAssortativity);

		log.append("Average degree in Barabasi-Albert network: " + baAverageDegree + "\n");
		System.out.println("Average degree in Barabasi-Albert network: " + baAverageDegree);
		log.append("Average clustering coefficient in Barabasi-Albert network: " + baAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in Barabasi-Albert network: " + baAverageClusteringCoefficient);
		log.append("Assortativity in Barabasi-Albert network: " + baAssortativity + "\n");
		System.out.println("Assortativity in Barabasi-Albert network: " + baAssortativity);

		log.append("Average degree in geometric network: " + gmAverageDegree + "\n");
		System.out.println("Average degree in geometric network: " + gmAverageDegree);
		log.append("Average clustering coefficient in geometric network: " + gmAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in geometric network: " + gmAverageClusteringCoefficient);
		log.append("Assortativity in geometric network: " + gmAssortativity + "\n");
		System.out.println("Assortativity in geometric network: " + gmAssortativity);

		log.append("Average degree in duplication network: " + dmAverageDegree + "\n");
		System.out.println("Average degree in duplication network: " + dmAverageDegree);
		log.append("Average clustering coefficient in duplication network: " + dmAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in duplication network: " + dmAverageClusteringCoefficient);
		log.append("Assortativity in duplication network: " + dmAssortativity + "\n");
		System.out.println("Assortativity in duplication network: " + dmAssortativity);

		log.append("Average degree in Forest-fire network: " + ffmAverageDegree + "\n");
		System.out.println("Average degree in Forest-fire network: " + ffmAverageDegree);
		log.append("Average clustering coefficient in Forest-fire network: " + ffmAverageClusteringCoefficient + "\n");
		System.out.println("Average clustering coefficient in Forest-fire network: " + ffmAverageClusteringCoefficient);
		log.append("Assortativity in Forest-fire network: " + ffmAssortativity + "\n");
		System.out.println("Assortativity in Forest-fire network: " + ffmAssortativity);

		Object[][] rows = {
				{"Target Network", dbAverageDegree, dbAverageClusteringCoefficient, dbAssortativity},
				{"Shuffling", smAverageDegree, smAverageClusteringCoefficient, smAssortativity},
				{"Erdos-Renyii", erAverageDegree, erAverageClusteringCoefficient, erAssortativity},
				{"Watts-Strogaz", wsAverageDegree, wsAverageClusteringCoefficient, wsAssortativity},
				{"Barabasi-Albert", baAverageDegree, baAverageClusteringCoefficient, baAssortativity},
				{"Geometric", gmAverageDegree, gmAverageClusteringCoefficient, gmAssortativity},
				{"Duplication", dmAverageDegree, dmAverageClusteringCoefficient, dmAssortativity},
				{"Forest-fire", ffmAverageDegree, ffmAverageClusteringCoefficient, ffmAssortativity}
		};
		Object[] cols = {"Network", "Average degree", "Average clustering coefficient", "Assortativity"};

		JTable table = new JTable(rows, cols);
		table.setPreferredScrollableViewportSize(new Dimension(600, 130));

		JOptionPane.showMessageDialog(activator.getCySwingApplication().getJFrame(),
				new JScrollPane(table), Common.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}
    
    @Override
	public void cancel() {
		//this.interrupted = true;
	}
}