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
package netmatch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import netmatch.algorithm.Graph;

import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.NetworkViewRenderer;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class ResultsTableModel extends AbstractTask implements TableModel {
	private String[] columnNames = {"Match number", "Occurrences", "Nodes", "Image"};
	private Object[][] data;
	
	private static final int graphPicSize = 80;
	
	private CySwingAppAdapter adapter;
	
	private CyNetwork network;
	private ArrayList<int[]> complexes;
	private Hashtable<String,Long> table;
	private int[] occurrences;
	
	private boolean isApproximate;
	private ArrayList allPaths;
	
	private ArrayList<CyNetwork> networksList;
	private ArrayList<ArrayList<String>> matches;
	
	private ArrayList<HashMap<CyNode,CyNode>> nodesList;
	
	private TaskMonitor taskMonitor;
	private boolean interrupted;
	private ResultsPanel resultsPanel;
	
	private ResultsTableModel model;
	private int howToShow;
	private int numMatches;
	
	private static boolean completedSuccessfully;
	
	public ResultsTableModel() {
		//data = new Object[0][3];
		data = new Object[0][4];
	}
	
	public ResultsTableModel(CyNetwork network, ArrayList complexes, Hashtable table, 
			int howToShow, boolean isApproximate, ArrayList allPaths, CySwingAppAdapter adapter) {
		this.network = network;
		this.complexes = complexes;
		this.table = table;
		this.howToShow = howToShow;
		this.isApproximate = isApproximate;
		this.allPaths = allPaths;
		this.adapter = adapter;
		model = this;
		data = new Object[complexes.size()][columnNames.length];
	}
	
	public ResultsTableModel(CyNetwork network, ArrayList complexes, Hashtable table, 
			int howToShow, boolean isApproximate, CySwingAppAdapter adapter) {
		this.network = network;
		this.complexes = complexes;
		this.table = table;
		this.howToShow = howToShow;
		this.isApproximate = isApproximate;
		this.adapter = adapter;
		model = this;
		data = new Object[complexes.size()][columnNames.length];
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		taskMonitor = tm;
		
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		
		try {
			//networksList = convertComplexListToNetworkList(complexes, network);
			//howToShow = 0;
			numMatches = complexes.size();
			
			taskMonitor.setProgress(-1.0);
			taskMonitor.setStatusMessage("Loading Results... (Step 1 of 2)");
          				
			matches = getMatchesListFromComplexesList(complexes, network);
			occurrences = new int[complexes.size()];
			
			nodesList = new ArrayList<HashMap<CyNode,CyNode>>();
			
			if (interrupted)
				return;
			
			taskMonitor.setStatusMessage("Drawing Results... (Step 2 of 2)");
			taskMonitor.setProgress(-1.0);
          							
			/*if (numMatches > 500) {
				SwingUtilities.invokeLater(new Runnable() {
	          		
					@Override
					public void run() {
						howToShow = chooseHowToShow(numMatches);*/
						if (howToShow == 0) {
							loadResultsInGraphicMode();
							if (isApproximate)
								resultsPanel.set(complexes, network, model, isApproximate, allPaths);
							else
								resultsPanel.set(complexes, network, model, isApproximate);
						}
						else if (howToShow == 1) {
							loadResultsInTextMode();
							if (isApproximate)
								resultsPanel.set(complexes, network, model, isApproximate, allPaths);
							else
								resultsPanel.set(complexes, network, model, isApproximate);
						}
						resultsPanel.doLayout();
					/*}
				});
			}
			else {
				loadResultsInGraphicMode();
				
				SwingUtilities.invokeLater(new Runnable() {
	          		
					@Override
					public void run() {  	
	          			resultsPanel.set(complexes, network, model);
	          		}
				});
			}*/
				
			completedSuccessfully = true;
		}
		catch (NullPointerException e) {
			System.out.println(e);
		}
		catch (Exception e) {
			completedSuccessfully = false;
			System.out.println(e);
			throw new Exception("Please check data!", e);
		}
		catch (Error e) {
			completedSuccessfully = false;
			System.out.println(e);
			throw new Exception("NetMatch cancelled", e);
		}
		finally {
			//No op
		}
	}
	
	private void loadResultsInGraphicMode() {
		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		
		//if (complexes.size() > 0 && howToShow != 2) {
			CytoPanel resPanel = adapter.getCySwingApplication().getCytoPanel(CytoPanelName.EAST);
			resPanel.setState(CytoPanelState.DOCK);
			resultsPanel = new ResultsPanel(adapter);
			csr.registerService(resultsPanel, CytoPanelComponent.class, new Properties());
			resPanel.setSelectedIndex(resPanel.getCytoPanelComponentCount()-1);
		//}
			
		for (int i = 0; i < numMatches; i++) {
			int tmp[] = (int[])complexes.get(i);
			Arrays.sort(tmp); //TODO Controllare
			String s = "";
			for (int j = 0; j < tmp.length-1; j++) 
				s += tmp[j]+"-";
			s += tmp[tmp.length-1];
			String dupl = Long.toString(((Long)table.get(s)).longValue());
			occurrences[i] = Integer.parseInt(dupl);
			
			ArrayList<String> match = matches.get(i);
			
			HashSet<String> paths = null;
			if (isApproximate)
				paths = getPaths(i, match);
			
			data[i][0] = (new Integer(i+1)).toString();
			data[i][1] = dupl;
			if (!isApproximate)
				data[i][2] = getNodeNameList(match, i);
			else
				data[i][2] = getNodeNameList(match, paths, i);
			//if (howToShow == 0)
				data[i][3] = new ImageIcon(convertNetworkToImage((int[]) complexes.get(i), i, graphPicSize, graphPicSize));
			//else
				//data[i][3] = "No Image";
				
			if (interrupted)
				return;
			//taskMonitor.setProgress(((i * 100) / complexes.size()));
		}
	}
	
	private void loadResultsInTextMode() {
		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		
		//if (complexes.size() > 0 && howToShow != 2) {
			CytoPanel resPanel = adapter.getCySwingApplication().getCytoPanel(CytoPanelName.EAST);
			resPanel.setState(CytoPanelState.DOCK);
			resultsPanel = new ResultsPanel(adapter);
			csr.registerService(resultsPanel, CytoPanelComponent.class, new Properties());
			resPanel.setSelectedIndex(resPanel.getCytoPanelComponentCount()-1);
		//}
			
		for (int i = 0; i < numMatches; i++) {
			int tmp[] = (int[])complexes.get(i);
			Arrays.sort(tmp); //TODO Controllare
			String s = "";
			for (int j = 0; j < tmp.length-1; j++)
				s += tmp[j]+"-";
			s += tmp[tmp.length-1];
			String dupl = Long.toString(((Long)table.get(s)).longValue());
			occurrences[i] = Integer.parseInt(dupl);
			
			ArrayList<String> match = matches.get(i);
			
			HashSet<String> paths = null;
			if (isApproximate)
				paths = getPaths(i, match);
			
			data[i][0] = (new Integer(i+1)).toString();
			data[i][1] = dupl;
			if (!isApproximate)
				data[i][2] = getNodeNameList(match, i);
			else
				data[i][2] = getNodeNameList(match, paths, i);
			//if (howToShow == 0)
				//data[i][3] = new ImageIcon(convertNetworkToImage((int[]) complexes.get(i), graphPicSize, graphPicSize));
			//else
				data[i][3] = "No Image";
				
			resultsPanel.doLayout();
				
			if (interrupted)
				return;
			//taskMonitor.setProgress(((i * 100) / complexes.size()));
		}
	}
	
	private HashSet<String> getPaths(int matchNumber, ArrayList<String> match) {
		HashSet<String> pathsSet = new HashSet<String>();
		
		List<CyNode> networkNodesList = network.getNodeList();
		ArrayList paths = (ArrayList)allPaths.get(matchNumber);
		Iterator pathsIterator = paths.iterator();
		while(pathsIterator.hasNext()) {
			Object element = pathsIterator.next();
			Class<?> type = element.getClass();
			Graph.Edge[] edges = (Graph.Edge[]) element;
			for (int i = 0; i < edges.length; i++) {
				Graph.Edge edge = edges[i];
				int sourceId = edge.getSource();
				int targetId = edge.getTarget();
				CyNode source = networkNodesList.get(sourceId);
				CyNode target = networkNodesList.get(targetId);
				
				CyRow sourceRow = network.getRow(source);
				CyTable sourceTbl = network.getDefaultNodeTable();
				ArrayList<CyColumn> sourceColumns = (ArrayList<CyColumn>) sourceTbl.getColumns();
				CyColumn sourceIdCol = sourceColumns.get(1);
				String sourceAttrName = sourceIdCol.getName();
				Class<?> type2 = sourceIdCol.getType();
				String sourceAttr = (String)sourceRow.get(sourceAttrName, type2);
				
				CyRow targetRow = network.getRow(source);
				CyTable targetTbl = network.getDefaultNodeTable();
				ArrayList<CyColumn> targetColumns = (ArrayList<CyColumn>) sourceTbl.getColumns();
				CyColumn targetIdCol = targetColumns.get(1);
				String targetAttrName = sourceIdCol.getName();
				Class<?> type3 = sourceIdCol.getType();
				String targetAttr = (String)sourceRow.get(targetAttrName, type3);
				
				if (!match.contains(sourceAttr))
					pathsSet.add(sourceAttr);
				if (!match.contains(targetAttr))
					pathsSet.add(targetAttr);
			}
		}
		
		return pathsSet;
	}
	
	private StringBuffer getNodeNameList(ArrayList<String> match, int matchNumber) {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iterator = match.iterator();
		while(iterator.hasNext()) {
			String node = iterator.next();
			sb.append(node);
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}
		return sb;
	}
	
	private StringBuffer getNodeNameList(ArrayList<String> match, HashSet<String> paths, int matchNumber) {
		StringBuffer sb = getNodeNameList(match, matchNumber);
		
		if (paths.size() > 0)
			sb.append(", ");
		
		Iterator<String> iterator = paths.iterator();
		while(iterator.hasNext()) {
			String node = iterator.next();
			if (!match.contains(node))
				sb.append(node);
			if (iterator.hasNext())
				sb.append(", ");
		}
		
		//workaround because there is a control at line 343, but probably you can remove this
		if (sb.subSequence(sb.length()-2, sb.length()-1).equals(", ")) 
			sb.replace(sb.length()-2, sb.length()-1, "");
		return sb;
	}
	
	public int[] getOccurrences() {
		return occurrences;
	}
	
	public ArrayList getComplexes() {
		return complexes;
	}
	
	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	public Class getColumnClass(int c) {
	    return getValueAt(0, c).getClass();
	}
	
	public static boolean isCompletedSuccessfully() {
		return completedSuccessfully;
	}
    
    public String getTitle() {
        return "NetMatch";
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
		//No op
	}
	
	private Image convertNetworkToImage(int[] complex, int matchNumber, final int height, final int width) {
		CyNetworkViewManager nvm = adapter.getCyNetworkViewManager();
		Collection<CyNetworkView> networkViewSet = nvm.getNetworkViews(network);
		Iterator<CyNetworkView> networkViewSetIterator = networkViewSet.iterator();
		CyNetworkView networkView = null;
		if (networkViewSetIterator.hasNext()) {
			networkView = networkViewSetIterator.next();
		}
		
		final CyNetwork subNetwork = getSubNetworkFromMatches(complex, matchNumber);
		HashMap<CyNode,CyNode> nodesMap = nodesList.get(matchNumber);
		
		VisualMappingManager manager = adapter.getVisualMappingManager();
		final VisualStyle vs = NetworkUtils.createSubNetworkVisualStyle(adapter);
		//final VisualStyle vs = getSubNetworkStyle(manager);
		
		CyNetworkViewFactory cnvf = adapter.getCyNetworkViewFactory();
		final CyNetworkView subNetworkView = cnvf.createNetworkView(subNetwork);
		
		subNetworkView.setVisualProperty(BasicVisualLexicon.NETWORK_WIDTH, new Double(width));
		subNetworkView.setVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT, new Double(height));
		
		for (View<CyNode> nv : subNetworkView.getNodeViews()) {
			// Node position
			final double x;
			final double y;
			
			CyNode n = nv.getModel();
			/*CyNode netNode = nodesMap.get(n); //TODO rivedere
			
			if (networkView != null && networkView.getNodeView(netNode) != null) { 
				x = networkView.getNodeView(netNode).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				y = networkView.getNodeView(netNode).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			}
			else {*/
				x = (subNetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH) + 100) * Math.random();
				y = (subNetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT) + 100) * Math.random();
			//}
			
			nv.setLockedValue(BasicVisualLexicon.NODE_X_LOCATION, x); 
			nv.setLockedValue(BasicVisualLexicon.NODE_Y_LOCATION, y);
			
			nv.setLockedValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
		}
		
		SpringEmbeddedLayouter layouter = new SpringEmbeddedLayouter();
		layouter.setGraphView(subNetworkView);
		
		//subNetworkView.fitContent();
		//subNetworkView.updateView();
		
		final Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = (Graphics2D) image.getGraphics();
		
		//SwingUtilities.invokeLater(new Runnable() {
			
			//@Override
			//public void run() {
				try {
					final Dimension size = new Dimension(width, height);

					JPanel panel = new JPanel();
					panel.setPreferredSize(size);
					panel.setSize(size);
					panel.setMinimumSize(size);
					panel.setMaximumSize(size);
					panel.setBackground((Color) vs.getDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT));

					JWindow window = new JWindow();
					window.getContentPane().add(panel, BorderLayout.CENTER);
					
					CyApplicationManager am = adapter.getCyApplicationManager();
					NetworkViewRenderer nvr = am.getCurrentNetworkViewRenderer();
					RenderingEngineFactory<CyNetwork> renderingEngineFactory = nvr.getRenderingEngineFactory("");
					RenderingEngine<CyNetwork> re = renderingEngineFactory.createRenderingEngine(panel, subNetworkView);

					vs.apply(subNetworkView);
					subNetworkView.fitContent();
					subNetworkView.updateView();
					window.pack();
					window.repaint();

					re.createImage(width, height);
					re.printCanvas(g);
					g.dispose();
				} 
				catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			//}
		//});
				
		layouter.resetDoLayout();
		
		return image;
	}
	
	private CyNetwork getSubNetworkFromMatches(int[] complex, int matchNumber) {
		CyNetwork subNetwork;
		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		CyNetworkFactory netFact = csr.getService(CyNetworkFactory.class);
		//subNetwork = netFact.createNetwork();
		subNetwork = NetworkUtils.createNetwork(adapter, "Match", Common.NODE_LABEL_ATTR, Common.EDGE_LABEL_ATTR);
		
		HashMap<CyNode,CyNode> subNodesMap = new HashMap<CyNode,CyNode>();
		HashMap<CyEdge,CyEdge> subEdgesMap = new HashMap<CyEdge,CyEdge>();
		HashMap<CyNode,CyNode> nodesMap = new HashMap<CyNode,CyNode>();
		
		List<CyNode> networkNodesList = network.getNodeList();
		//List<CyEdge> networkEdgesList = network.getEdgeList();
		Set<CyNode> matchNodesList = new HashSet<CyNode>();
		Set<CyEdge> matchEdgesList = new HashSet<CyEdge>();
		Set<CyNode> pathsNodesList = new HashSet<CyNode>();
		Set<CyEdge> pathsEdgesList = new HashSet<CyEdge>();
		
		for (int i = 0; i < complex.length; i++) {
			CyNode node = networkNodesList.get(complex[i]);
			matchNodesList.add(node);
		}
		
		Iterator<CyNode> sourcesIterator = matchNodesList.iterator();
		while(sourcesIterator.hasNext()) {
			CyNode sourceNode = sourcesIterator.next();
			Iterator<CyNode> targetsIterator = matchNodesList.iterator();
			while(targetsIterator.hasNext()) {
				CyNode targetNode = targetsIterator.next();
				List<CyEdge> edges = network.getConnectingEdgeList(sourceNode, targetNode, CyEdge.Type.ANY);
				Iterator<CyEdge> edgesIterator = edges.iterator();
				while(edgesIterator.hasNext()) {
					CyEdge edge = edgesIterator.next();
					matchEdgesList.add(edge);
				}
			}
		}
		
		Iterator<CyNode> nodesIterator = matchNodesList.iterator();
		while(nodesIterator.hasNext()) {
			CyNode node = nodesIterator.next();
			CyRow nodeRow = network.getRow(node);
			CyTable nodeTbl = network.getDefaultNodeTable();						
			ArrayList<CyColumn> columns = (ArrayList<CyColumn>) nodeTbl.getColumns();
			
			CyNode newNode = subNetwork.addNode();
			Iterator<CyColumn> columnsIterator = columns.iterator();
			while(columnsIterator.hasNext()) {
				CyColumn column = columnsIterator.next();
				
				String columnName = column.getName();
				Class<?> type = column.getType();
				//if (!columnName.equals("SUID")) {
				if (!Collection.class.isAssignableFrom(type)) {
					Object columnValue = nodeRow.get(columnName, type);
					CyTable subNodeTbl = subNetwork.getDefaultNodeTable();
					if (columnValue != null && subNodeTbl.getColumn(columnName) != null)
						subNodeTbl.getRow(newNode.getSUID()).set(columnName, columnValue);
				}
			}//while(columnsIterator.hasNext())
			
			subNodesMap.put(node, newNode);
			nodesMap.put(newNode, node);
		}//while(nodesIterator.hasNext())
		
		Iterator<CyEdge> edgesIterator = matchEdgesList.iterator();
		while(edgesIterator.hasNext()) {
			CyEdge edge = edgesIterator.next();
			CyRow edgeRow = network.getRow(edge);
			CyTable edgeTbl = network.getDefaultEdgeTable();
			ArrayList<CyColumn> columns = (ArrayList<CyColumn>) edgeTbl.getColumns();
			
			CyNode source = edge.getSource();
			CyNode target = edge.getTarget();
			
			CyNode childSource = subNodesMap.get(source);
			CyNode childTarget = subNodesMap.get(target);
			
			CyEdge newEdge = subNetwork.addEdge(childSource, childTarget, true);
			Iterator<CyColumn> columnsIterator = columns.iterator();
			while(columnsIterator.hasNext()) {
				CyColumn column = columnsIterator.next();
				
				String columnName = column.getName();
				Class<?> type = column.getType();
				//if (!columnName.equals("SUID")) {
				if (!Collection.class.isAssignableFrom(type)) {
					Object columnValue = edgeRow.get(columnName, type);
					CyTable subEdgeTbl = subNetwork.getDefaultEdgeTable();
					if (columnValue != null && subEdgeTbl.getColumn(columnName) != null)
						subEdgeTbl.getRow(newEdge.getSUID()).set(columnName, columnValue);
				}
			}//while(columnsIterator.hasNext())
			
			subEdgesMap.put(edge, newEdge); //TODO controllare
		}//while(edgesIterator.hasNext())
		
		if (isApproximate) {
			nodesIterator = pathsNodesList.iterator();
			while(nodesIterator.hasNext()) {
				CyNode node = nodesIterator.next();
				CyRow nodeRow = network.getRow(node);
				CyTable nodeTbl = network.getDefaultNodeTable();					
				ArrayList<CyColumn> columns = (ArrayList<CyColumn>) nodeTbl.getColumns();
				
				if (!subNodesMap.containsKey(node)) {
					CyNode newNode = subNetwork.addNode();
					Iterator<CyColumn> columnsIterator = columns.iterator();
					while(columnsIterator.hasNext()) {
						CyColumn column = columnsIterator.next();
						
						String columnName = column.getName();
						Class<?> type = column.getType();
						//if (!columnName.equals("SUID")) {
						if (!Collection.class.isAssignableFrom(type)) {
							Object columnValue = nodeRow.get(columnName, type);
							if (columnValue != null)
								subNetwork.getDefaultNodeTable().getRow(newNode.getSUID()).set(columnName, columnValue);
						}
					}
					
					subNodesMap.put(node, newNode);
					nodesMap.put(newNode, node);
				}//if (!nodesMap.containsKey(node))
			}//while (nodesIterator.hasNext())
			
			edgesIterator = pathsEdgesList.iterator();
			while(edgesIterator.hasNext()) {
				CyEdge edge = edgesIterator.next();
				CyRow edgeRow = network.getRow(edge);
				CyTable edgeTbl = network.getDefaultEdgeTable();
				ArrayList<CyColumn> columns = (ArrayList<CyColumn>) edgeTbl.getColumns();
				
				CyNode source = edge.getSource();
				CyNode target = edge.getTarget();
				
				CyNode childSource = subNodesMap.get(source);
				CyNode childTarget = subNodesMap.get(target);
				
				if (!subEdgesMap.containsKey(edge)) { //TODO controllare
					CyEdge newEdge = subNetwork.addEdge(childSource, childTarget, true);
				
					Iterator<CyColumn> columnsIterator = columns.iterator();
					while(columnsIterator.hasNext()) {
						CyColumn column = columnsIterator.next();
						
						String columnName = column.getName();
						Class<?> type = column.getType();
						//if (!columnName.equals("SUID")) {
						if (!Collection.class.isAssignableFrom(type)) {
							Object columnValue = edgeRow.get(columnName, type);
							if (columnValue != null)
								subNetwork.getDefaultEdgeTable().getRow(newEdge.getSUID()).set(columnName, columnValue);
						}
					}
					
					subEdgesMap.put(edge, newEdge); //TODO controllare
				}//if (!edgesMap.containsKey(edge))
			}//while (edgeIterator.hasNext())
			
			ArrayList paths = (ArrayList)allPaths.get(matchNumber);
			Iterator pathsIterator = paths.iterator();
			while(pathsIterator.hasNext()) {
				Object element = pathsIterator.next();
				Class<?> type = element.getClass();
				Graph.Edge[] edges = (Graph.Edge[]) element;
				for (int i = 0; i < edges.length; i++) {
					Graph.Edge edge = edges[i];
					int sourceId = edge.getSource();
					int targetId = edge.getTarget();
					CyNode source = networkNodesList.get(sourceId);
					CyNode target = networkNodesList.get(targetId);
					
					pathsNodesList.add(source);
					pathsNodesList.add(target);
				}
			}
			
			sourcesIterator = pathsNodesList.iterator();
			while(sourcesIterator.hasNext()) {
				CyNode sourceNode = sourcesIterator.next();
				Iterator<CyNode> targetsIterator = pathsNodesList.iterator();
				while(targetsIterator.hasNext()) {
					CyNode targetNode = targetsIterator.next();
					List<CyEdge> edges = network.getConnectingEdgeList(sourceNode, targetNode, CyEdge.Type.ANY);
					edgesIterator = edges.iterator();
					while(edgesIterator.hasNext()) {
						CyEdge edge = edgesIterator.next();
						pathsEdgesList.add(edge);
					}
				}
			}
			
			nodesIterator = pathsNodesList.iterator();
			while(nodesIterator.hasNext()) {
				CyNode node = nodesIterator.next();
				CyRow nodeRow = network.getRow(node);
				CyTable nodeTbl = network.getDefaultNodeTable();						
				ArrayList<CyColumn> columns = (ArrayList<CyColumn>) nodeTbl.getColumns();
				
				if (!subNodesMap.containsKey(node)) {
					CyNode newNode = subNetwork.addNode();
					Iterator<CyColumn> columnsIterator = columns.iterator();
					while(columnsIterator.hasNext()) {
						CyColumn column = columnsIterator.next();
						
						String columnName = column.getName();
						Class<?> type = column.getType();
						//if (!columnName.equals("SUID")) {
						if (!Collection.class.isAssignableFrom(type)) {
							Object columnValue = nodeRow.get(columnName, type);
							if (columnValue != null)
								subNetwork.getDefaultNodeTable().getRow(newNode.getSUID()).set(columnName, columnValue);
						}
					}
					
					subNodesMap.put(node, newNode);
					nodesMap.put(newNode, node);
				}//if (!nodesMap.containsKey(node))
			}//while (nodesIterator.hasNext())
			
			edgesIterator = pathsEdgesList.iterator();
			while(edgesIterator.hasNext()) {
				CyEdge edge = edgesIterator.next();
				CyRow edgeRow = network.getRow(edge);
				CyTable edgeTbl = network.getDefaultEdgeTable();
				ArrayList<CyColumn> columns = (ArrayList<CyColumn>) edgeTbl.getColumns();
				
				if (!subEdgesMap.containsKey(edge)) { //TODO controllare
					CyNode source = edge.getSource();
					CyNode target = edge.getTarget();
					
					CyNode childSource = subNodesMap.get(source);
					CyNode childTarget = subNodesMap.get(target);
					
					CyEdge newEdge = subNetwork.addEdge(childSource, childTarget, true);
					Iterator<CyColumn> columnsIterator = columns.iterator();
					while(columnsIterator.hasNext()) {
						CyColumn column = columnsIterator.next();
						
						String columnName = column.getName();
						Class<?> type = column.getType();
						//if (!columnName.equals("SUID")) {
						if (!Collection.class.isAssignableFrom(type)) {
							Object columnValue = edgeRow.get(columnName, type);
							if (columnValue != null)
								subNetwork.getDefaultEdgeTable().getRow(newEdge.getSUID()).set(columnName, columnValue);
						}
					}
					
					subEdgesMap.put(edge, newEdge); //TODO controllare
				}//if (!edgesMap.containsKey(edge))
			}//while (edgeIterator.hasNext())
		}//if (isApproximate)
		
		nodesList.add(nodesMap);
		
		return subNetwork;
	}
	
	private ArrayList<ArrayList<String>> getMatchesListFromComplexesList(ArrayList complexList, CyNetwork sourceNetwork) {
		ArrayList<ArrayList<String>> matchesList = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < complexList.size(); i++) {
			matchesList.add(getMatchesListFromComplexes((int[])complexList.get(i), sourceNetwork));
			if (interrupted)
				return null;
			taskMonitor.setProgress(((i * 100) / complexList.size()));
		}
		
		return matchesList;
	}
	
	private ArrayList<String> getMatchesListFromComplexes(int[] complex, CyNetwork sourceNetwork) {
		ArrayList<String> match = new ArrayList<String>();
		
		List<CyNode> nodeList = sourceNetwork.getNodeList();
		
		for (int i = 0; i < complex.length; i++) {
			CyNode node = nodeList.get(complex[i]);
			
			CyRow nodeRow = sourceNetwork.getRow(node);
			CyTable nodeTbl = sourceNetwork.getDefaultNodeTable();
			
			ArrayList<CyColumn> columns = (ArrayList<CyColumn>) nodeTbl.getColumns();
			CyColumn identifierCol = columns.get(1);
			String attrName = identifierCol.getName();
			
			Class<?> type = identifierCol.getType();
			String nodeValue = (String)nodeRow.get(attrName, type);
			
			match.add(nodeValue);
		}
		
		return match;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		//no op
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		//no op
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		//no op
	}
}