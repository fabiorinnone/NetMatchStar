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
package netmatch.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import netmatch.utils.Common;
import netmatch.utils.Pair;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskIterator;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class NetworkUtils {
	
	/**
	 * Create a three-chain network
	 * @param adapter
	 * @return
	 */
	public static CyNetwork createThreeChainNetwork(CySwingAppAdapter adapter) {
		CyNetwork threeChain = createMotifNetwork(adapter, "QueryNetwork"+"-unamed-" + Common.indexN);
		
		CyNode node1 = threeChain.addNode();
		CyNode node2 = threeChain.addNode();
		CyNode node3 = threeChain.addNode();
		
		CyTable nodeTable = threeChain.getDefaultNodeTable();
		
		nodeTable.getRow(node1.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node2.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node3.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		
		if (nodeTable.getColumn(Common.NODE_ID_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_ID_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_ID_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_ID_ATTR, "n3");
		}
		if (nodeTable.getColumn(Common.NODE_NAME_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_NAME_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_NAME_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_NAME_ATTR, "n3");
		}
		
		CyEdge edge1 = threeChain.addEdge(node1, node2, true);
		CyEdge edge2 = threeChain.addEdge(node2, node3, true);
		
		CyTable edgeTable = threeChain.getDefaultEdgeTable();
		
		edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		
		if (edgeTable.getColumn(Common.EDGE_ID_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_ID_ATTR, "n1 (e1) n2");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_ID_ATTR, "n2 (e2) n3");
		}
		if (edgeTable.getColumn(Common.EDGE_NAME_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_NAME_ATTR, "n1 (e1) n2");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_NAME_ATTR, "n2 (e2) n3");
		}
		if (edgeTable.getColumn(Common.EDGE_INTER_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_INTER_ATTR, "e1");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_INTER_ATTR, "e2");
		}
		
		Long suid = threeChain.getSUID();
		Common.motifsMap.put(suid, Common.THREE_CHAIN);
		
		return threeChain;
	}
	
	/**
	 * Create a feed-forward-loop network
	 * @param adapter
	 * @return
	 */
	public static CyNetwork createFeedForwardLoopNetwork(CySwingAppAdapter adapter) {
		CyNetwork feedForwardLoop = createMotifNetwork(adapter, "QueryNetwork"+"-unamed-" + Common.indexN);
		
		CyNode node1 = feedForwardLoop.addNode();
		CyNode node2 = feedForwardLoop.addNode();
		CyNode node3 = feedForwardLoop.addNode();
		
		CyTable nodeTable = feedForwardLoop.getDefaultNodeTable();
		
		nodeTable.getRow(node1.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node2.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node3.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		
		if (nodeTable.getColumn(Common.NODE_ID_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_ID_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_ID_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_ID_ATTR, "n3");
		}
		if (nodeTable.getColumn(Common.NODE_NAME_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_NAME_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_NAME_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_NAME_ATTR, "n3");
		}
		
		CyEdge edge1 = feedForwardLoop.addEdge(node1, node2, true);
		CyEdge edge2 = feedForwardLoop.addEdge(node1, node3, true);
		CyEdge edge3 = feedForwardLoop.addEdge(node2, node3, true);
		
		CyTable edgeTable = feedForwardLoop.getDefaultEdgeTable();
		
		edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		
		if (edgeTable.getColumn(Common.EDGE_ID_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_ID_ATTR, "n1 (e1) n2");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_ID_ATTR, "n1 (e2) n3");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_ID_ATTR, "n2 (e3) n3");
		}
		if (edgeTable.getColumn(Common.EDGE_NAME_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_NAME_ATTR, "n1 (e1) n2");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_NAME_ATTR, "n1 (e2) n3");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_NAME_ATTR, "n2 (e3) n3");
		}
		if (edgeTable.getColumn(Common.EDGE_INTER_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_INTER_ATTR, "e1");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_INTER_ATTR, "e2");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_INTER_ATTR, "e3");
		}
		
		Long suid = feedForwardLoop.getSUID();
		Common.motifsMap.put(suid, Common.FEED_FORWARD_LOOP);
		
		return feedForwardLoop;
	}
	
	/**
	 * Create a bi-parallel network
	 * @param adapter
	 * @return
	 */
	public static CyNetwork createBiParallelNetwork(CySwingAppAdapter adapter) {
		CyNetwork biParallel = createMotifNetwork(adapter, "QueryNetwork"+"-unamed-" + Common.indexN);
		
		CyNode node1 = biParallel.addNode();
		CyNode node2 = biParallel.addNode();
		CyNode node3 = biParallel.addNode();
		CyNode node4 = biParallel.addNode();
		
		CyTable nodeTable = biParallel.getDefaultNodeTable();
		
		nodeTable.getRow(node1.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node2.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node3.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node4.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		
		if (nodeTable.getColumn(Common.NODE_ID_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_ID_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_ID_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_ID_ATTR, "n3");
			nodeTable.getRow(node4.getSUID()).set(Common.NODE_ID_ATTR, "n4");
		}
		if (nodeTable.getColumn(Common.NODE_NAME_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_NAME_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_NAME_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_NAME_ATTR, "n3");
			nodeTable.getRow(node4.getSUID()).set(Common.NODE_NAME_ATTR, "n4");
		}
		
		CyEdge edge1 = biParallel.addEdge(node1, node2, true);
		CyEdge edge2 = biParallel.addEdge(node1, node3, true);
		CyEdge edge3 = biParallel.addEdge(node2, node4, true);
		CyEdge edge4 = biParallel.addEdge(node3, node4, true);
		
		CyTable edgeTable = biParallel.getDefaultEdgeTable();
		
		edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		
		if (edgeTable.getColumn(Common.EDGE_ID_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_ID_ATTR, "n1 (e1) n2");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_ID_ATTR, "n1 (e2) n3");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_ID_ATTR, "n2 (e3) n4");
			edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_ID_ATTR, "n3 (e4) n4");
		}
		if (edgeTable.getColumn(Common.EDGE_NAME_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_NAME_ATTR, "n1 (e1) n2");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_NAME_ATTR, "n1 (e2) n3");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_NAME_ATTR, "n2 (e3) n4");
			edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_NAME_ATTR, "n3 (e4) n4");
		}
		if (edgeTable.getColumn(Common.EDGE_INTER_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_INTER_ATTR, "e1");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_INTER_ATTR, "e2");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_INTER_ATTR, "e3");
			edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_INTER_ATTR, "e4");
		}
		
		Long suid = biParallel.getSUID();
		Common.motifsMap.put(suid, Common.BI_PARALLEL);
		
		return biParallel;
	}
	
	/**
	 * Create a bi-fan network
	 * @param adapter
	 * @return
	 */
	public static CyNetwork createBiFanNetwork(CySwingAppAdapter adapter) {
		CyNetwork biFan = createMotifNetwork(adapter, "QueryNetwork"+"-unamed-" + Common.indexN);
		
		CyNode node1 = biFan.addNode();
		CyNode node2 = biFan.addNode();
		CyNode node3 = biFan.addNode();
		CyNode node4 = biFan.addNode();
		
		CyTable nodeTable = biFan.getDefaultNodeTable();
		
		nodeTable.getRow(node1.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node2.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node3.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		nodeTable.getRow(node4.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		
		if (nodeTable.getColumn(Common.NODE_ID_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_ID_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_ID_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_ID_ATTR, "n3");
			nodeTable.getRow(node4.getSUID()).set(Common.NODE_ID_ATTR, "n4");
		}
		if (nodeTable.getColumn(Common.NODE_NAME_ATTR) != null) {
			nodeTable.getRow(node1.getSUID()).set(Common.NODE_NAME_ATTR, "n1");
			nodeTable.getRow(node2.getSUID()).set(Common.NODE_NAME_ATTR, "n2");
			nodeTable.getRow(node3.getSUID()).set(Common.NODE_NAME_ATTR, "n3");
			nodeTable.getRow(node4.getSUID()).set(Common.NODE_NAME_ATTR, "n4");
		}
			
		CyEdge edge1 = biFan.addEdge(node1, node3, true);
		CyEdge edge2 = biFan.addEdge(node1, node4, true);
		CyEdge edge3 = biFan.addEdge(node2, node3, true);
		CyEdge edge4 = biFan.addEdge(node2, node4, true);
		
		CyTable edgeTable = biFan.getDefaultEdgeTable();
		
		edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		
		if (edgeTable.getColumn(Common.EDGE_ID_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_ID_ATTR, "n1 (e1) n3");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_ID_ATTR, "n1 (e2) n4");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_ID_ATTR, "n2 (e3) n3");
			edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_ID_ATTR, "n2 (e4) n4");
		}
		if (edgeTable.getColumn(Common.EDGE_NAME_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_NAME_ATTR, "n1 (e1) n3");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_NAME_ATTR, "n1 (e2) n4");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_NAME_ATTR, "n2 (e3) n3");
			edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_NAME_ATTR, "n2 (e4) n4");
		}
		if (edgeTable.getColumn(Common.EDGE_INTER_ATTR) != null) {
			edgeTable.getRow(edge1.getSUID()).set(Common.EDGE_INTER_ATTR, "e1");
			edgeTable.getRow(edge2.getSUID()).set(Common.EDGE_INTER_ATTR, "e2");
			edgeTable.getRow(edge3.getSUID()).set(Common.EDGE_INTER_ATTR, "e3");
			edgeTable.getRow(edge4.getSUID()).set(Common.EDGE_INTER_ATTR, "e4");
		}
		
		Long suid = biFan.getSUID();
		Common.motifsMap.put(suid, Common.BI_FAN);
		
		return biFan;
	}
	
	/**
	 * Create a m-to-n-fan network
	 * @param adapter
	 * @return
	 */
	public static CyNetwork createMtonFanNetwork(CySwingAppAdapter adapter) {
		CyNetwork mtonFan = createMotifNetwork(adapter, "QueryNetwork"+"-unamed-" + Common.indexN);
		
		ArrayList<Object> mn = new ArrayList<Object>();
        
		Object[] message = new Object[3];

	    JSpinner mSpinner = new JSpinner(new SpinnerNumberModel(4, 0, Integer.MAX_VALUE, 1));
	    //mSpinner.setValue(new Integer(4));
	    JSpinner nSpinner = new JSpinner(new SpinnerNumberModel(4, 0, Integer.MAX_VALUE, 1));
	    //nSpinner.setValue(new Integer(4));
	    //JTextField nTextField = new JTextField(25);
	    JLabel mLabel = new JLabel("m:");
	    mLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

	    JLabel nLabel = new JLabel("n: ");
	    nLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	    
	    JPanel pm = new JPanel(new BorderLayout(5, 5));
	    pm.add(mLabel, BorderLayout.WEST);
	    pm.add(mSpinner, BorderLayout.CENTER);
	    JPanel pn = new JPanel(new BorderLayout(5, 5));
	    pn.add(nLabel, BorderLayout.WEST);
	    pn.add(nSpinner, BorderLayout.CENTER);
	    message[0] = "<html>Please select <em>m</em> and <em>n</em>:</html>";
	    message[1] = pm;
	    message[2] = pn;
	    String[] options = {"OK", "Cancel"};
	    
	    int result = JOptionPane.showOptionDialog(
	    		adapter.getCySwingApplication().getJFrame(), //the parent that the dialog blocks
	    		message, //the dialog message array
	    		"Please configure m to n pattern", //the title of the dialog window
	    		JOptionPane.DEFAULT_OPTION, //option type
	    		JOptionPane.INFORMATION_MESSAGE, //message type
	    		null, //optional icon, use null to use the default icon
	    		options, //options string array, will be made into buttons
	    		options[0] //option that should be made into a default button
	    );
	    
	    if (result == 0) {
	    	mn.add(mSpinner.getValue());
	        mn.add(nSpinner.getValue());
	        
	        int m = ((Integer) mn.get(0)).intValue();
	        int n = ((Integer) mn.get(1)).intValue();
	        
	        ArrayList<CyNode> mNodes = new ArrayList<CyNode>(m);
	        ArrayList<CyNode> nNodes = new ArrayList<CyNode>(n);

	        CyTable nodeTable = mtonFan.getDefaultNodeTable();
	        for (int i = 0; i < m; i++) {
	        	CyNode node = mtonFan.addNode();
	        	nodeTable.getRow(node.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
	        	if (nodeTable.getColumn(Common.NODE_ID_ATTR) != null)
	        		nodeTable.getRow(node.getSUID()).set(Common.NODE_ID_ATTR, "n" + (i+1));
	        	if (nodeTable.getColumn(Common.NODE_NAME_ATTR) != null)
	        		nodeTable.getRow(node.getSUID()).set(Common.NODE_NAME_ATTR, "n" + (i+1));
	        	mNodes.add(node);
	        }
	        
	        for (int i = 0; i < n; i++) {
	        	 CyNode node = mtonFan.addNode();
	        	 nodeTable.getRow(node.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
	        	 if (nodeTable.getColumn(Common.NODE_ID_ATTR) != null)
	        		 nodeTable.getRow(node.getSUID()).set(Common.NODE_ID_ATTR, "n" + (m+i+1));
	        	 if (nodeTable.getColumn(Common.NODE_NAME_ATTR) != null)
	        		 nodeTable.getRow(node.getSUID()).set(Common.NODE_NAME_ATTR, "n" + (m+i+1));
	        	 nNodes.add(node);
	        }
	        
	        CyTable edgeTable = mtonFan.getDefaultEdgeTable();
	        int r = 1; //edges counter
	        for (int s = 0; s < m; s++) {
	        	for (int t = 0; t < n; t++) {
	        		CyNode sNode = mNodes.get(s); //m node
	        		CyNode tNode = nNodes.get(t); //n node
	        		CyEdge edge = mtonFan.addEdge(sNode, tNode, true);
	        		edgeTable.getRow(edge.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
	        		if (edgeTable.getColumn(Common.EDGE_ID_ATTR) != null)
	        			edgeTable.getRow(edge.getSUID()).set(Common.EDGE_ID_ATTR, "n" + (s+1)+" (e" + (r++)+") n" + (m+t+1));
	        		if (edgeTable.getColumn(Common.EDGE_NAME_ATTR) != null)
	        			edgeTable.getRow(edge.getSUID()).set(Common.EDGE_NAME_ATTR, "n" + (s+1)+" (e" + (r++)+") n" + (m+t+1));
	        		if (edgeTable.getColumn(Common.EDGE_INTER_ATTR) != null)
	        			edgeTable.getRow(edge.getSUID()).set(Common.EDGE_INTER_ATTR, "e" + (r++));
	        	}
	        }
	        
	        Long suid = mtonFan.getSUID();
	        Common.motifsMap.put(suid, Common.MTON_FAN);
	        Common.mtonFanMap.put(suid, new Pair<ArrayList<CyNode>>(mNodes, nNodes));
	        
	        return mtonFan;
	    }
	    
	    return null;
	}
	
	/**
	 * Configure the NetMatch default visual style for query networks
	 * @param vs
	 * @param adapter
	 */
	public static void configureQueryVisualStyle(VisualStyle vs, CySwingAppAdapter adapter) {
		Color myColor = new Color(51, 51, 51);
		
		//node style
		vs.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, myColor);
		vs.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 5.0);
		vs.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.WHITE);
		vs.setDefaultValue(BasicVisualLexicon.NODE_LABEL_COLOR, Color.BLACK);
		vs.setDefaultValue(BasicVisualLexicon.NODE_LABEL_FONT_SIZE, 18);
		vs.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
		vs.setDefaultValue(BasicVisualLexicon.NODE_SIZE, 50.0);
		vs.setDefaultValue(BasicVisualLexicon.NODE_WIDTH, 25.0);
		vs.setDefaultValue(BasicVisualLexicon.NODE_HEIGHT, 50.0);
		vs.setDefaultValue(BasicVisualLexicon.NODE_TRANSPARENCY, 255);
		vs.setDefaultValue(BasicVisualLexicon.NODE_SELECTED_PAINT, Color.RED);
		
		//edge style
		vs.setDefaultValue(BasicVisualLexicon.EDGE_LABEL_COLOR, Color.BLACK);
		vs.setDefaultValue(BasicVisualLexicon.EDGE_LABEL_FONT_SIZE, 24);
		vs.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, Color.RED);
		vs.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, myColor);
		vs.setDefaultValue(BasicVisualLexicon.EDGE_TRANSPARENCY, 255);
		vs.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, 2.0);
		vs.setDefaultValue(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE, ArrowShapeVisualProperty.ARROW);
		
		setVisualMappingFunction(adapter, vs, Common.NODE_QUERY_ATTR, Common.EDGE_QUERY_ATTR);
	}
	
	public static void setVisualMappingFunction(
			CySwingAppAdapter adapter, VisualStyle vs, String nodeAttribute, String edgeAttribute) {
		VisualMappingFunctionFactory vmff = adapter.getVisualMappingFunctionPassthroughFactory();
		
		if (edgeAttribute != null) {
			VisualMappingFunction<String, String> newEdgeVisualMappingFunction = vmff.createVisualMappingFunction(
					edgeAttribute, String.class, BasicVisualLexicon.EDGE_LABEL);
			vs.addVisualMappingFunction(newEdgeVisualMappingFunction);
		}
		
		if (nodeAttribute != null) {
			VisualMappingFunction<String, String> newNodeVisualMappingFunction = vmff.createVisualMappingFunction(
					nodeAttribute, String.class, BasicVisualLexicon.NODE_LABEL);
			vs.addVisualMappingFunction(newNodeVisualMappingFunction);
		}
	}
	
	/**
	 * Configure visual style for motifs network views
	 * @param networkView
	 * @param motifType
	 */
	public static void configureMotifVisualStyle(CyNetworkView networkView, Integer motifType) {
		if (motifType == Common.THREE_CHAIN)
			configureThreeChainVisualStyle(networkView);
		else if (motifType == Common.FEED_FORWARD_LOOP)
			configureFeedForwardLoopVisualStyle(networkView);
		else if (motifType == Common.BI_PARALLEL)
			configureBiParallelVisualStyle(networkView);
		else if (motifType == Common.BI_FAN) 
			configureBiFanVisualStyle(networkView);
		else
			configureMtonFanVisualStyle(networkView);
	}
	
	/**
	 * Configure visual style for m-to-n-fan network view
	 * @param networkView
	 */
	private static void configureMtonFanVisualStyle(CyNetworkView networkView) {
		Long suid = networkView.getModel().getSUID();
		Pair<ArrayList<CyNode>> nodesPair = Common.mtonFanMap.get(suid);
		ArrayList<CyNode> mNodes = nodesPair.getFirst();
		ArrayList<CyNode> nNodes = nodesPair.getSecond();
		
		int m = mNodes.size();
		int n = nNodes.size();
		int shift = 0;
		if (m < n) 
			shift = 105 * (n - m);
		
		CyNode first = mNodes.get(0);
		View<CyNode> firstView = networkView.getNodeView(first);
		Double x = firstView.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
		Double y = firstView.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
		
		for (int i = 1; i < m; i++) {
			CyNode node = mNodes.get(i);
			View<CyNode> view = networkView.getNodeView(node);
			view.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x + 180.0 * i + shift);
			view.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
		}
		
		if (m > n)
			shift = 105 * (m - n);
		else
			shift = 0;
		
		for (int i = 0; i < n; i++) {
			CyNode node = nNodes.get(i);
			View<CyNode> view = networkView.getNodeView(node);
			view.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x + 180.0 * i + shift);
			view.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y + 180.0);
		}
	}

	/**
	 * Configure visual style for bi-fan network view
	 * @param networkView
	 */
	private static void configureBiFanVisualStyle(CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		List<CyNode> nodes = network.getNodeList();
		CyNode node1 = nodes.get(0);
		CyNode node2 = nodes.get(1);
		CyNode node3 = nodes.get(2);
		CyNode node4 = nodes.get(3);
		
		View<CyNode> view1 = networkView.getNodeView(node1);
		View<CyNode> view2 = networkView.getNodeView(node2);
		View<CyNode> view3 = networkView.getNodeView(node3);
		View<CyNode> view4 = networkView.getNodeView(node4);
		
		Double x = view1.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
		Double y = view1.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
		view2.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x - 180.0);
		view2.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
		view3.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
		view3.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 180.0);
		view4.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x - 180.0);
		view4.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 180.0);
	}

	/**
	 * Configure visual style for bi-parallel network view
	 * @param networkView
	 */
	private static void configureBiParallelVisualStyle(CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		List<CyNode> nodes = network.getNodeList();
		CyNode node1 = nodes.get(0);
		CyNode node2 = nodes.get(1);
		CyNode node3 = nodes.get(2);
		CyNode node4 = nodes.get(3);
		
		View<CyNode> view1 = networkView.getNodeView(node1);
		View<CyNode> view2 = networkView.getNodeView(node2);
		View<CyNode> view3 = networkView.getNodeView(node3);
		View<CyNode> view4 = networkView.getNodeView(node4);
		
		Double x = view1.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
		Double y = view1.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
		view2.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x + 100.0);
		view2.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 100.0);
		view3.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x - 100.0);
		view3.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 100.0);
		view4.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
		view4.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 200.0);
	}
	
	/**
	 * Configure visual style for feed-forward-loop network view
	 * @param networkView
	 */
	private static void configureFeedForwardLoopVisualStyle(CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		List<CyNode> nodes = network.getNodeList();
		CyNode node1 = nodes.get(0);
		CyNode node2 = nodes.get(1);
		CyNode node3 = nodes.get(2);
		
		View<CyNode> view1 = networkView.getNodeView(node1);
		View<CyNode> view2 = networkView.getNodeView(node2);
		View<CyNode> view3 = networkView.getNodeView(node3);
		
		Double x = view1.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
		Double y = view1.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
		view2.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x - 100.0);
		view2.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 100.0);
		view3.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
		view3.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 200.0);	
	}

	/**
	 * Configure visual style for three-chain network view
	 * @param networkView
	 */
	private static void configureThreeChainVisualStyle(CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		List<CyNode> nodes = network.getNodeList();
		CyNode node1 = nodes.get(0);
		CyNode node2 = nodes.get(1);
		CyNode node3 = nodes.get(2);
		
		View<CyNode> view1 = networkView.getNodeView(node1);
		View<CyNode> view2 = networkView.getNodeView(node2);
		View<CyNode> view3 = networkView.getNodeView(node3);
		
		Double x = view1.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
		Double y = view1.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
		view2.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
		view2.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 120.0);
		view3.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
		view3.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y - 240.0);
	}
	
	/**
	 * Create a motif query network
	 * @param adapter
	 * @param name
	 * @return
	 */
	private static CyNetwork createMotifNetwork(CySwingAppAdapter adapter, String name) {
		return createNetwork(adapter, name, Common.NODE_QUERY_ATTR, Common.EDGE_QUERY_ATTR);
	}
	
	/**
	 * Create a network with a nodes attribute and an edges attribute
	 * @param adapter Cytoscape adapter
	 * @param name name of the network
	 * @param nodeAttr attribute for nodes
	 * @param edgeAttr attribute for edges
	 * @return
	 */
	public static CyNetwork createNetwork(CySwingAppAdapter adapter, String name,
			String nodeAttr, String edgeAttr) {
		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		CyNetworkFactory netFact = csr.getService(CyNetworkFactory.class);
		CyNetwork cyNetwork = netFact.createNetwork();
		cyNetwork.getRow(cyNetwork).set(CyNetwork.NAME, name);
		
		CyTable nodeTable = cyNetwork.getDefaultNodeTable();
		nodeTable.createColumn(nodeAttr, String.class, true); //immutable
		
		CyTable edgeTable = cyNetwork.getDefaultEdgeTable();
		edgeTable.createColumn(edgeAttr, String.class, true); //immutable
		
		return cyNetwork;
	}
	
	/**
	 * Create a network with a list of nodes and edges attributes
	 * @param adapter Cytoscape adapter
	 * @param name name of the network
	 * @param nodeAttrList attributes list for nodes
	 * @param edgeAttrList attributes list for edges
	 * @return
	 */
	public static CyNetwork createNetwork(CySwingAppAdapter adapter, String name, 
			ArrayList<String> nodeAttrList, ArrayList<String> edgeAttrList) {
		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		CyNetworkFactory netFact = csr.getService(CyNetworkFactory.class);
		CyNetwork cyNetwork = netFact.createNetwork();
		cyNetwork.getRow(cyNetwork).set(CyNetwork.NAME, name);
		
		CyTable nodeTable = cyNetwork.getDefaultNodeTable();
		Iterator<String> nodeAttrIterator = nodeAttrList.iterator();
		while(nodeAttrIterator.hasNext()) {
			String nodeAttr = nodeAttrIterator.next();
			nodeTable.createColumn(nodeAttr, String.class, true); //immutable
		}
		
		CyTable edgeTable = cyNetwork.getDefaultEdgeTable();
		Iterator<String> edgeAttrIterator = edgeAttrList.iterator();
		while(edgeAttrIterator.hasNext()) {
			String edgeAttr = edgeAttrIterator.next();
			edgeTable.createColumn(edgeAttr, String.class, true); //immutable
		}
		
		return cyNetwork;
	}
	
	/**
	 * Create a network view for a network
	 * @param adapter
	 * @param network
	 * @return
	 */
	private static CyNetworkView createNetworkView(CySwingAppAdapter adapter, CyNetwork network) {
		CyNetworkViewFactory cnvf = adapter.getCyNetworkViewFactory();
		CyNetworkView cyView = cnvf.createNetworkView(network);
		
		return cyView;
	}
	
	/**
	 * Add a network view to Cytoscape
	 * @param adapter
	 * @param network
	 */
	public static void addNetworkView(CySwingAppAdapter adapter, CyNetwork network) {
		CyNetworkView cyView = createNetworkView(adapter, network);
		
		CyNetworkViewManager cnvm = adapter.getCyNetworkViewManager();		
		cnvm.addNetworkView(cyView);
		cyView.updateView();
		
		CyLayoutAlgorithmManager clam = adapter.getCyLayoutAlgorithmManager();
		CyLayoutAlgorithm alg = clam.getDefaultLayout();
		TaskIterator ti = alg.createTaskIterator(
				cyView, alg.getDefaultLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);
		adapter.getTaskManager().execute(ti);
	}
	
	/**
	 * Add a network to Cytoscape
	 */
	public static void addNetwork(CySwingAppAdapter adapter, CyNetwork network) {
		CyNetworkManager cnm = adapter.getCyNetworkManager();
		cnm.addNetwork(network);
	}
		
	/**
	 * Check if a query is a motif
	 * @param suid
	 * @return
	 */
	public static boolean isAMotif(Long suid) {
		return Common.motifsMap.containsKey(suid);
	}
	
	/**
	 * Create a network view visual style for a child network
	 * @param adapter
	 * @return
	 */
	public static VisualStyle createSubNetworkVisualStyle(CySwingAppAdapter adapter) {
		VisualStyleFactory vsf = adapter.getVisualStyleFactory();
		
		VisualStyle subNetworkStyle = vsf.createVisualStyle("Netmatch Output Style");
		
		//subNetworkStyle.setDefaultValue(BasicVisualLexicon.NODE_SIZE, 20.0);
		//subNetworkStyle.setDefaultValue(BasicVisualLexicon.NODE_WIDTH, 20.0);
		//subNetworkStyle.setDefaultValue(BasicVisualLexicon.NODE_HEIGHT, 20.0);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.NODE_PAINT, Color.RED);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.NODE_SELECTED_PAINT, Color.RED);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.RED);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 0.0);
		
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, 2.0);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.EDGE_PAINT, Color.BLUE);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.EDGE_UNSELECTED_PAINT, Color.BLUE);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.BLUE);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.EDGE_SELECTED_PAINT, Color.BLUE);
		subNetworkStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, Color.BLUE);
		
		return subNetworkStyle;
	}
}
