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
package it.unict.dmi.netmatchstar.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import it.unict.dmi.netmatchstar.CyActivator;
import it.unict.dmi.netmatchstar.graph.Graph;
import it.unict.dmi.netmatchstar.graph.GraphLoader;
import it.unict.dmi.netmatchstar.utils.Common;
import it.unict.dmi.netmatchstar.view.ResultsTableModel;
import it.unict.dmi.netmatchstar.view.WestPanel;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.PanelTaskManager;

@SuppressWarnings("rawtypes")
public class MatchTask extends AbstractTask {
	
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
	private ArrayList allPaths;
	
	private long totalMatches;
	private long distinctMatches;
	
	private TaskMonitor taskMonitor;
	private boolean interrupted;
	private JTextArea log;
	
	private CyActivator activator;
	protected int howToShow;
	private Hashtable<String,Long> table;
	
	private static boolean completedSuccessfully;
	
	public MatchTask(CyNetwork t, CyNetwork q, ArrayList tel, ArrayList tnl, 
			String qeaa, String qnaa, JPanel frame2, CyActivator activator) {
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
	
	public MatchTask(CyNetwork t, CyNetwork q, ArrayList tel, ArrayList tnl, 
			String qeaa, String qnaa, boolean iqa, boolean iqu, JPanel frame2, CyActivator activator) {
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
			long t_start = System.currentTimeMillis();
			
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
			if(Common.LABELED) {
				db.setNodeComparator(new ExactNodeComparator());
				db.setEdgeComparator(new ExactEdgeComparator());
			}
			else {
				db.setNodeComparator(new ApproxNodeComparator());
				db.setEdgeComparator(new ApproxEdgeComparator());
			}
			if(interrupted)
				return;
			
			System.out.println("Loading network time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
			t_start = System.currentTimeMillis();
			
			System.out.println("Create Query Loader (Step 3 of 5)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Create Query Loader (Step 3 of 5)");
			//if (!isApproximate) 
				qLoader = loadGraphFromNetwork(query, qea, qna);
			if(interrupted)
				return;
			System.out.println("Create Query Graph Data (Step 4 of 5)");
			taskMonitor.setProgress(0.0);
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
			
			System.out.println("Loading query time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
			
			RIMatch m;
			if (!isUnlabeled) 
				m = new RIMatch(q, db);
			else
				m = new RIMatch(q, db, isApproximate, approxPaths);
      	
			System.out.println("Matching... (Step 3 of 5)");
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Matching... (Step 3 of 5)");
		
			Set<Integer> nodiTarget = db.nodes().keySet();
			if (Common.DOMAINS)
				m.match(nodiTarget.iterator());
			else
				m.match_simple(nodiTarget.iterator());
			
			array = m.getMatchesList2();
			totalMatches = m.getNofMatches();
			
			//printMatches(db, array);
			
	      	allPaths = m.getApproximatePaths();
	      	
	      	completedSuccessfully = true;
	      	
	      	log = WestPanel.getLog();
		    
	      	if(isCompletedSuccessfully())  {
	      		table = m.getMatchesOccurrences();
	      		distinctMatches = table.size();
	      		
	      		System.out.println("Found " + totalMatches + " total matches!");
	      		System.out.println("Found " + distinctMatches + " distinct matches!");
	      		
	      		SwingUtilities.invokeLater(new Runnable() {
	          		public void run() {
	          			log.append("Found " + totalMatches + " total matches!\r\n"); 
	    	      		log.append("Found " + distinctMatches + " distinct matches!\r\n"); 
	    	      		
	    	      		if (distinctMatches > 250)
	    	      			howToShow = chooseHowToShow(distinctMatches);
	    	      		else
	    	      			howToShow = 0;
	    	      		if (howToShow != 2 && totalMatches > 0) {
	    	      			CyServiceRegistrar csr = activator.getCyServiceRegistrar();
	    					PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
	    					TaskIterator taskIterator = new TaskIterator();
	    					ResultsTableModel resultsTask = null;
	    					if (isApproximate)
	    						resultsTask = new ResultsTableModel(target, array, table, 
	    								howToShow, isApproximate, allPaths, activator);
	    					else 
	    						resultsTask = new ResultsTableModel(target, array, table, 
	    								howToShow, isApproximate, activator);
	    					taskIterator.append(resultsTask);
	    					dialogTaskManager.execute(taskIterator);
	    	      		}
             		}
	          	});
	      		
	      		if(interrupted)
	      			return;
	      	}
	      	else {
	      		//log.setText("");
	      	}
	      	
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
	
	public void printMatches(ArrayList array) {
		Iterator iterator = array.iterator();
		System.out.print("{");
		while(iterator.hasNext()) {
			int[] match = (int[]) iterator.next();
			System.out.print("[");
			for (int i = 0; i < match.length; i++) {
				System.out.print(match[i]);
				if (i != match.length - 1)
					System.out.print(",");
			}
			System.out.print("]");
			if (iterator.hasNext())
				System.out.print(",");
		}
		System.out.println("}");
	}
	
	public void printMatches(Graph target, ArrayList array) {
		Iterator iterator = array.iterator();
		System.out.print("{");
		while(iterator.hasNext()) {
			int[] match = (int[]) iterator.next();
			System.out.print("[");
			for (int i = 0; i < match.length; i++) {
				//System.out.print(match[i]);
				System.out.print(target.nodes().get(match[i]).getAttribute().toString());
				if (i != match.length - 1)
					System.out.print(",");
			}
			System.out.print("]");
			if (iterator.hasNext())
				System.out.print(",");
		}
		System.out.println("}");
	}
	
	public GraphLoader loadGraphFromNetwork(CyNetwork network, ArrayList edgeAttr,
											ArrayList nodeAttr) throws Exception {
    	GraphLoader loader;
        Hashtable names = new Hashtable();
        int i, k;
        boolean any;
        i = k = 0;
        loader = new GraphLoader(activator, frame);
        int size = network.getNodeCount() + network.getEdgeCount();
        //int count = 0;
        //int nodeCount = network.getNodeCount();
        for (CyNode node : network.getNodeList()) {
        	//taskMonitor.setProgress(2 * (double)count++ / nodeCount);
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
        //count = 0;
        //int edgeCount = network.getEdgeCount();
        for (CyEdge edge : network.getEdgeList()) {
        	//taskMonitor.setProgress(2 * (double)count++ / edgeCount);
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
    
	private int chooseHowToShow(long matches) {
	    Object[] message = new Object[4];
	    message[0] = "The number of matches is " + matches + ". Choose one of the following options:";
	    ButtonGroup g = new ButtonGroup();
	    JRadioButton c1 = new JRadioButton("Show results in text and graphic mode.", false);
	    JRadioButton c2 = new JRadioButton("Show results in text mode.", true);
	    JRadioButton c3 = new JRadioButton("Don't show results.", false);
	    g.add(c1);
	    g.add(c2);
	    g.add(c3);
	    message[1] = c1;
	    message[2] = c2;
	    message[3] = c3;
	    String[] options = {"OK"};

	    JOptionPane.showOptionDialog(activator.getCySwingApplication().getJFrame(), // the parent that the dialog blocks
	    		message, // the dialog message array
	    		Common.APP_NAME, // the title of the dialog window
	    		JOptionPane.DEFAULT_OPTION, // option type
	    		JOptionPane.INFORMATION_MESSAGE, // message type
	    		null, // optional icon, use null to use the default icon
	    		options, // options string array, will be made into buttons
	    		options[0] // option that should be made into a default button
	    );
	    
	    if (c1.isSelected())
	    	return 0;
	    else if (c2.isSelected())
	    	return 1;
	    else
	    	return 2;
	}
}