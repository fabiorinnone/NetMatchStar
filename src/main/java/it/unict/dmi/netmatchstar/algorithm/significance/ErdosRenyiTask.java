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
package it.unict.dmi.netmatchstar.algorithm.significance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import it.unict.dmi.netmatchstar.CyActivator;
import it.unict.dmi.netmatchstar.algorithm.*;
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
public class ErdosRenyiTask extends AbstractTask {
	private int m_netNum;
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

	private ArrayList<int[]> array;

	private TaskMonitor taskMonitor;
	private boolean interrupted;
	private double evalue;
	private CyActivator activator;
	private int N;
	private JTextArea log;
	
	private long seedValue;
	private boolean customSeed;
	
	private static boolean completedSuccessfully;
	
	private double m_numMatchesNet;
	private double m_averageNumMatches;
	private double m_sigmaNumMatches;
	private double m_eValue;
	private double m_zScore;
	private double m_numSignificativeNets;
	
	public ErdosRenyiTask(int n, boolean direct, CyNetwork t, 
			CyNetwork q, ArrayList tel, ArrayList tnl, String qeaa, String qnaa, 
			JPanel frame2, CyActivator activator) {
		m_netNum = n;
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
		
		customSeed = false;
	}
	
	public ErdosRenyiTask(int n, boolean direct, CyNetwork t, 
			CyNetwork q, ArrayList tel, ArrayList tnl, String qeaa, String qnaa, 
			long seedValue, JPanel frame2, CyActivator activator) {
		this(n, direct, t, q, tel, tnl, qeaa, qnaa, frame2, activator);
		this.seedValue = seedValue;
		
		customSeed = true;
	}
	
	public ErdosRenyiTask(int n, boolean direct, CyNetwork t, 
			CyNetwork q, ArrayList tel, ArrayList tnl, String qeaa, String qnaa, 
			boolean iqa, boolean iqu, JPanel frame2, CyActivator activator) {
		m_netNum = n;
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
		
		customSeed = false;
	}
	
	public ErdosRenyiTask(int n, boolean direct, CyNetwork t, 
			CyNetwork q, ArrayList tel, ArrayList tnl, String qeaa, String qnaa, 
			boolean iqa, boolean iqu, long seedValue, JPanel frame2, CyActivator activator) {
		this(n, direct, t, q, tel, tnl, qeaa, qnaa, iqa, iqu, frame2, activator);
		this.seedValue = seedValue;
		
		customSeed = true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void run(TaskMonitor tm) throws Exception {
		taskMonitor = tm;
		
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		
		try {
			System.out.println("Create Network Loader (Step 1 of 6)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Network Loader (Step 1 of 6)");
			GraphLoader dbLoader = loadGraphFromNetwork(target, tea, tna);
			if(interrupted)
				return;
			System.out.println("Create Network Graph Data (Step 2 of 6)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Network Graph Data (Step 2 of 6)");
			Graph db = new Graph(dbLoader, Common.DIRECTED);
			if(interrupted)
				return;
			System.out.println("Create Query Loader (Step 3 of 6)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Query Loader (Step 3 of 6)");
			//if (!isApproximate) 
				qLoader = loadGraphFromNetwork(query, qea, qna);
			if(interrupted)
				return;
			System.out.println("Create Query Graph Data (Step 4 of 6)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Query Graph Data (Step 4 of 6)");
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
			
			RIMatch m;
			if (!isUnlabeled) 
				m = new RIMatch(q, db);
			else
				m = new RIMatch(q, db, isApproximate, approxPaths);
      	
			System.out.println("Matching... (Step 5 of 6)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Matching... (Step 5 of 6)");
			
			if (!isUnlabeled) 
				m = new RIMatch(q, db);
			else
				m = new RIMatch(q, db, isApproximate, approxPaths);
		
			Set<Integer> nodiTarget = db.nodes().keySet();
			m.match_simple(nodiTarget.iterator());
			
			array = m.getMatchesList();

			completedSuccessfully = true;
	      	    
	      	//System.out.println("NetMatch* elapsed time: " + elapsedTime);
	      		      	
	      	//ArrayList l = m.getMatchesList();
	      	int numMatchesNet = array.size();

	      	int total = 0;
	      	int totalSquare = 0;
	      	int numGreater = 0;

	      	if(interrupted)
	      		return;
	      	
	      	System.out.println("Verification... (Step 6 of 6)");
	      	taskMonitor.setProgress(0.0);
	      	taskMonitor.setStatusMessage("Verification... (Step 6 of 6)");
	      	
	      	for(int i = 0; i < m_netNum; i++) {
    	        taskMonitor.setProgress(((double)i / m_netNum));
    	            	        
    	        int numNodes = target.getNodeCount();
    	        int numEdges = target.getEdgeCount();
    	        
    	        RandomGenerator randomGenerator;
    	        if (!customSeed) 
    	        	randomGenerator = new RandomGenerator(db.nodes(), numNodes, numEdges, m_direct);
    	        else 
    	        	randomGenerator = new RandomGenerator(db.nodes(), numNodes, numEdges, m_direct, seedValue);
    	        
    	        db = randomGenerator.createErdosRenyi();
    	        
    	        System.out.println("Matching random graph " + i + "...");
    	        
    	        if (!isUnlabeled) 
    				m = new RIMatch(q, db);
    			else
    				m = new RIMatch(q, db, isApproximate, approxPaths);
    	    
    	        nodiTarget = db.nodes().keySet();
    			m.match_simple(nodiTarget.iterator());
    			
       	        ArrayList l2 = m.getMatchesList();
    	        int numMatches = l2.size();

    	        System.out.println("Number of matches: " + numMatches);

    	        total += numMatches;
    	        totalSquare += Math.pow(numMatches, 2);
    	        if(numMatches >= numMatchesNet)
    	        	numGreater++; 
    	        if(interrupted)
    	        	return;
	      	}
    	      
	      	m_numMatchesNet = numMatchesNet;
	      	m_averageNumMatches = (double)total / m_netNum;
	      	m_sigmaNumMatches = Math.sqrt(((double)totalSquare) / m_netNum - Math.pow(m_averageNumMatches, 2));
	      	m_numSignificativeNets = numGreater;
	      	m_eValue = m_numSignificativeNets / m_netNum;
	      	m_zScore = (m_numMatchesNet - m_averageNumMatches) / m_sigmaNumMatches;

	      	completedSuccessfully = true;
    	      
	        N = WestPanel.getRandomNets().getValue();
	        
	        log = WestPanel.getLog();
		    
	        if(isCompletedSuccessfully()) {
	        	if (m_numMatchesNet == 0) {
	        		System.out.println("The network has not any occurrences of the query");
	        		SwingUtilities.invokeLater(new Runnable() {
  		          		public void run() {
  		          			log.append("The network has not any occurrences of the query\n");
  		          			JOptionPane.showMessageDialog(activator.getCySwingApplication().getJFrame(),
	          						"The network has not any occurrences of the query\n", 
	          						Common.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
  		          		}
	        		});
	        	}
	        	else {
	        		SwingUtilities.invokeLater(new Runnable() {
  		          		public void run() {
  		          			log.append("Occurrences in real network: " + m_numMatchesNet + "\n");
  		          			System.out.println("Occurrences in real network: " + m_numMatchesNet);
  		          			log.append("Avg. occurrences in randomized networks: " + m_averageNumMatches + "\n");
  		          			System.out.println("Avg. occurrences in randomized networks: " + m_averageNumMatches);
  		          			log.append("s.d. occurrences in randomized networks: " + m_sigmaNumMatches + "\n");
  		          			System.out.println("s.d. occurrences in randomized networks: " + m_sigmaNumMatches);
  		          			evalue = m_eValue;
  		          			if (evalue == 0) {
  		          				log.append("E-value < " + (1.0/N) + "\n");
  		          				System.out.println("E-value < " + (1.0/N));
  		          			}
  		          			else {
  		          				log.append("E-value: " + evalue + "\n");
  		          				System.out.println("E-value: " + evalue);
  		          			}
  		          			log.append("Z-score: " + m_zScore + "\n");
  		          			System.out.println("Z-score: " + m_zScore);
  		          			if (evalue == 0)
  		          				JOptionPane.showMessageDialog(activator.getCySwingApplication().getJFrame(), "Number of "
  		          						+ "occurrences in the real network: " + m_numMatchesNet + "\n" +
										"Average occurrences in the randomized networks: " + m_averageNumMatches + "\n" +
										"Standard deviation occurrences in the randomized networks: " + m_sigmaNumMatches + "\n" +
										"E-value < " + (1.0/N) + "\n" +
										"Z-score: " + m_zScore + "\n",
										Common.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
  		          			else
  	  		          			JOptionPane.showMessageDialog(activator.getCySwingApplication().getJFrame(), "Number of "
  	  		          					+ "occurrences in the real network: " + m_numMatchesNet + "\n" +
										"Average occurrences in the randomized networks: " + m_averageNumMatches + "\n" +
										"Standard deviation of occurrences in the randomized networks: " + m_sigmaNumMatches + "\n" +
										"E-value: " + evalue + "\n" +
										"Z-score: " + m_zScore + "\n",
										Common.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
  		          		}
  		          	});
  		          	
	        	}
  		        if(interrupted)
	        		return;
	        }
	        
	      	System.out.println("Task completted");
	      	
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
			throw new Exception(Common.APP_NAME + "cancelled", e);
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
    
    @Override
	public void cancel() {
		//this.interrupted = true;
	}
}