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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import it.unict.dmi.netmatchstar.CyActivator;
import it.unict.dmi.netmatchstar.utils.Resources;
import it.unict.dmi.netmatchstar.utils.Resources.ImageName;
import it.unict.dmi.netmatchstar.graph.Graph;

import it.unict.dmi.netmatchstar.utils.Common;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskIterator;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class ResultsPanel extends JPanel implements CytoPanelComponent {
	private JPanel panel;
	private JTable table;
	private JScrollPane scrollPane;
	private ResultsTableModel model;
	private final int defaultRowHeight = Common.imageSize;
	private boolean openAsNewChild = false;
	private CyNetwork network;
	//private CyNetworkView originalInputNetworkView;
	//private HashMap hmNetworkNames;
	private int[] occurrences;
	private JButton saveButton;
	private JButton closeButton;
	private JCheckBox newWindowCheckBox;
	private boolean isApproximate;
	private ArrayList allPaths;
	//private boolean showPics;
	
	private CyActivator activator;
	
	public ResultsPanel(CyActivator activator) {
		this.activator = activator;
		
		network = null;
	    //showPics = false;
	    setLayout(new BorderLayout());
	    //hmNetworkNames = new HashMap();
	    //isApproximate = false;
	    //allPaths = null;
	    panel = new JPanel();
	    panel.setLayout(new BorderLayout());
	    panel.setBackground(Color.WHITE);
	    table = new JTable() {
	    	public String getToolTipText(MouseEvent e) {
	    		Point p = e.getPoint();
	    		int rowIndex = rowAtPoint(p);
	    		int colIndex = columnAtPoint(p);
	    		int realColumnIndex = convertColumnIndexToModel(colIndex);
	    		String tip = getColumnName(realColumnIndex);
	    		if(realColumnIndex == 0 || realColumnIndex == 1)
	    			tip += ":" + getValueAt(rowIndex, colIndex);
	    		return tip;
	    	}
    	};
	    table.setRowHeight(defaultRowHeight);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    table.setDefaultRenderer(String.class, new CenterAndBoldRenderer());
	    table.setDefaultRenderer(StringBuffer.class, new JTextAreaRenderer()); 
	    ListSelectionModel rowSM = table.getSelectionModel();
	    rowSM.addListSelectionListener(new TableRowSelectionHandler()); 
	    scrollPane = new JScrollPane(table);
	    scrollPane.getViewport().setBackground(Color.WHITE);
	    panel.add(scrollPane, BorderLayout.CENTER);
	    JPanel bottomPanel = new JPanel();
	    newWindowCheckBox = new JCheckBox("Create a new child network", false);
	    newWindowCheckBox.setToolTipText("<html>If checked, a new child network of<br>the selected match will be created.<br>" +
	    		"Otherwise, the occurrence of the<br>query match in the network window<br>will be highlighted.</html>");
	    newWindowCheckBox.addItemListener(new ResultsPanel.NewWindowCheckBoxAction());
	    newWindowCheckBox.setEnabled(false);
	    bottomPanel.add(newWindowCheckBox);
	    saveButton = new JButton("Save");
	    saveButton.setToolTipText("Save result summary to a file.");
	    saveButton.setEnabled(false);
	    bottomPanel.add(saveButton);
	    closeButton = new JButton("Close");
	    closeButton.setToolTipText("Close " + Common.APP_NAME + " Results Panel.");
	    bottomPanel.add(closeButton);
	    panel.add(bottomPanel, BorderLayout.SOUTH);
	    add(panel, BorderLayout.CENTER);
		
	    closeButton.addActionListener(new ResultsPanel.CloseAction(this));
	    //saveButton.setEnabled(true);
	}
	
	public void set(ArrayList complexes, CyNetwork network, ResultsTableModel tab, boolean isApproximate, ArrayList allPaths) {
		this.network = network;
		this.isApproximate = isApproximate;
		this.allPaths = allPaths;
		newWindowCheckBox.setSelected(false); 
	    model = tab;
	    occurrences = model.getOccurrences();
	    table.setModel(model);
	    table.setVisible(true);
	    initColumnSizes(table);
	    saveButton.addActionListener(new SaveAction(complexes, network, occurrences));
	    saveButton.setEnabled(true);
	    newWindowCheckBox.setEnabled(true);
	}
	
	public void set(ArrayList complexes, CyNetwork network, ResultsTableModel tab, boolean isApproximate) {
		this.network = network;
		this.isApproximate = isApproximate;
		newWindowCheckBox.setSelected(false); 
	    model = tab;
	    occurrences = model.getOccurrences();
	    table.setModel(model);
	    table.setVisible(true);
	    initColumnSizes(table);
	    saveButton.addActionListener(new SaveAction(complexes, network, occurrences));
	    saveButton.setEnabled(true);
	    newWindowCheckBox.setEnabled(true);
	}
	
	public void clear() {
		network = null;
		ActionListener al[] = saveButton.getActionListeners();
		for(int i = 0;i < al.length;i++)
			saveButton.removeActionListener(al[i]);
    	saveButton.setEnabled(false);
    	newWindowCheckBox.setEnabled(false);
	    table.setVisible(false);
	}
	
	public int[] getOccurrences() {
		return occurrences;
	}	
	
	/**
	 * Utility method to initialize the column sizes of the table
	 *
	 * @param table Table to initialize sizes for
	 */
	private void initColumnSizes(JTable table) {
	    TableColumn column;

	    for(int i = 0; i < 2; i++) {
	    	column = table.getColumnModel().getColumn(i);
	    	column.sizeWidthToFit();
	    }
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		//if(!enabled)
			//table.setModel(new ResultsTableModel());
		scrollPane.setEnabled(enabled);
		table.setEnabled(enabled);
	    newWindowCheckBox.setEnabled(enabled);
	    saveButton.setEnabled(enabled);
	}

	public ResultsTableModel getModel() {
		return model;
	}

	public void setModel(ResultsTableModel model) {
		this.model = model;
		table.setModel(model);
	}
	
	public CyNetwork getNetwork() {
		return network;
	}
	
	public boolean isOpenAsNewChild() {
		return openAsNewChild;
	}
		  
	public void setOpenAsNewChild(boolean value) {
		openAsNewChild = value;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public ArrayList getComplexes() {
		return model.getComplexes();
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	@Override
	public Icon getIcon() {
		final URL iconURL = Resources.getUrl(ImageName.LOGO);
		return new ImageIcon(iconURL);
	}

	@Override
	public String getTitle() {
		return "Results";
	}
	
	/**
	 * Handles the new window parameter choice
	 */
	private class NewWindowCheckBoxAction implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			openAsNewChild = e.getStateChange() != ItemEvent.DESELECTED;
		}
	}
	
	/**
	 * Handler to selects nodes in graph or create a new network when a row is selected
	 * Note: There is some fairly detailed logic in here to deal with all the cases that a user can interact
	 * with this dialog box. Be careful when editing this code.
	 */ 
	private class TableRowSelectionHandler implements ListSelectionListener {
		private CyNetwork childNetwork;
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting())
				return;
			
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if (!lsm.isSelectionEmpty()) {
				final int selectedRow = lsm.getMinSelectionIndex();
				
				ArrayList complexes = getComplexes();
				int[] complex = (int[])complexes.get(selectedRow);
				
				List<CyNode> networkNodesList = network.getNodeList();
				//List<CyEdge> networkEdgesList = network.getEdgeList();
				Set<CyNode> matchNodesList = new HashSet<CyNode>();
				Set<CyEdge> matchEdgesList = new HashSet<CyEdge>();
				Set<CyNode> pathsNodesList = new HashSet<CyNode>();
				Set<CyEdge> pathsEdgesList = new HashSet<CyEdge>();
				
				deselectAllNodesAndEdges(network);
				
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
				
				if (isApproximate) {
					ArrayList paths = (ArrayList)allPaths.get(selectedRow);
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
							Iterator<CyEdge> edgesIterator = edges.iterator();
							while(edgesIterator.hasNext()) {
								CyEdge edge = edgesIterator.next();
								pathsEdgesList.add(edge);
							}
						}
					}
				}//if (isApproximate)
				
				if (!openAsNewChild) {
					selectNodesOrEdges(matchNodesList, network);
					selectNodesOrEdges(matchEdgesList, network);
					if (isApproximate) {
						selectNodesOrEdges(pathsNodesList, network);
						selectNodesOrEdges(pathsEdgesList, network);
					}
				}
				else { //else if (openAsNewChild) {
					HashMap<CyNode,CyNode> nodesMap = new HashMap<CyNode,CyNode>();
					HashMap<CyEdge,CyEdge> edgesMap = new HashMap<CyEdge,CyEdge>();

					//CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
					CyNetworkFactory netFact = activator.getCyNetworkFactory();
					childNetwork = netFact.createNetwork();
					childNetwork.getRow(childNetwork).set(CyNetwork.NAME, "Match "+ (selectedRow + 1));
					CyNetworkManager cnm = activator.getCyNetworkManager();
					cnm.addNetwork(childNetwork);
					
					Iterator<CyNode> nodesIterator = matchNodesList.iterator();
					while(nodesIterator.hasNext()) {
						CyNode node = nodesIterator.next();
						CyRow nodeRow = network.getRow(node);
						CyTable nodeTbl = network.getDefaultNetworkTable();						
						ArrayList<CyColumn> columns = (ArrayList<CyColumn>) nodeTbl.getColumns();
						
						CyNode newNode = childNetwork.addNode();
						Iterator<CyColumn> columnsIterator = columns.iterator();
						while(columnsIterator.hasNext()) {
							CyColumn column = columnsIterator.next();
							
							String columnName = column.getName();
							Class<?> type = column.getType();
							//if (!columnName.equals("SUID")) {
							if (!Collection.class.isAssignableFrom(type)) {
								Object columnValue = nodeRow.get(columnName, type);
								if (columnValue != null)
									childNetwork.getDefaultNodeTable().getRow(newNode.getSUID()).set(columnName, columnValue);
							}
						}
						
						nodesMap.put(node, newNode);
					}
					
					Iterator<CyEdge> edgesIterator = matchEdgesList.iterator();
					while(edgesIterator.hasNext()) {
						CyEdge edge = edgesIterator.next();
						CyRow edgeRow = network.getRow(edge);
						CyTable edgeTbl = network.getDefaultEdgeTable();
						ArrayList<CyColumn> columns = (ArrayList<CyColumn>) edgeTbl.getColumns();
						
						CyNode source = edge.getSource();
						CyNode target = edge.getTarget();
						
						CyNode childSource = nodesMap.get(source);
						CyNode childTarget = nodesMap.get(target);
						
						CyEdge newEdge = childNetwork.addEdge(childSource, childTarget, true);
						Iterator<CyColumn> columnsIterator = columns.iterator();
						while(columnsIterator.hasNext()) {
							CyColumn column = columnsIterator.next();
							
							String columnName = column.getName();
							Class<?> type = column.getType();
							//if (!columnName.equals("SUID")) {
							if (!Collection.class.isAssignableFrom(type)) {
								Object columnValue = edgeRow.get(columnName, type);
								if (columnValue != null) {
									childNetwork.getDefaultEdgeTable().getRow(newEdge.getSUID()).set(columnName, columnValue);
								}
							}
						}
						
						edgesMap.put(edge, newEdge);
					}
					
					if (isApproximate) {
						nodesIterator = pathsNodesList.iterator();
						while(nodesIterator.hasNext()) {
							CyNode node = nodesIterator.next();
							CyRow nodeRow = network.getRow(node);
							CyTable nodeTbl = network.getDefaultNetworkTable();						
							ArrayList<CyColumn> columns = (ArrayList<CyColumn>) nodeTbl.getColumns();
							
							if (!nodesMap.containsKey(node)) {
								CyNode newNode = childNetwork.addNode();
								Iterator<CyColumn> columnsIterator = columns.iterator();
								while(columnsIterator.hasNext()) {
									CyColumn column = columnsIterator.next();
									
									String columnName = column.getName();
									Class<?> type = column.getType();
									//if (!columnName.equals("SUID")) {
									if (!Collection.class.isAssignableFrom(type)) {
										Object columnValue = nodeRow.get(columnName, type);
										if (columnValue != null)
											childNetwork.getDefaultNodeTable().getRow(newNode.getSUID()).set(columnName, columnValue);
									}
								}
								
								nodesMap.put(node, newNode);
							}
						}//while (nodesIterator.hasNext())
						
						edgesIterator = pathsEdgesList.iterator();
						while(edgesIterator.hasNext()) {
							CyEdge edge = edgesIterator.next();
							CyRow edgeRow = network.getRow(edge);
							CyTable edgeTbl = network.getDefaultEdgeTable();
							ArrayList<CyColumn> columns = (ArrayList<CyColumn>) edgeTbl.getColumns();
							
							CyNode source = edge.getSource();
							CyNode target = edge.getTarget();
							
							CyNode childSource = nodesMap.get(source);
							CyNode childTarget = nodesMap.get(target);
							
							if (!edgesMap.containsKey(edge)) { //TODO controllare
								CyEdge newEdge = childNetwork.addEdge(childSource, childTarget, true);
								Iterator<CyColumn> columnsIterator = columns.iterator();
								while(columnsIterator.hasNext()) {
									CyColumn column = columnsIterator.next();
									
									String columnName = column.getName();
									Class<?> type = column.getType();
									//if (!columnName.equals("SUID")) {
									if (!Collection.class.isAssignableFrom(type)) {
										Object columnValue = edgeRow.get(columnName, type);
										if (columnValue != null) {
											childNetwork.getDefaultEdgeTable().getRow(newEdge.getSUID()).set(columnName, columnValue);
										}
									}
								}
							}
						}//while (edgeIterator.hasNext())
					}//if (isApproximate)
					
					//Create Network View, Visual Style and Layout
					final SwingWorker<CyNetworkView, ?> worker = new SwingWorker<CyNetworkView, Object> () {
						
						@Override
						protected CyNetworkView doInBackground() throws Exception {
							CyNetworkViewFactory cnvf = activator.getCyNetworkViewFactory();
							CyNetworkView childNetworkView = cnvf.createNetworkView(childNetwork);
							CyNetworkViewManager cnvm = activator.getCyNetworkViewManager();
							cnvm.addNetworkView(childNetworkView);

							CySwingAppAdapter adapter = activator.getCySwingAppAdapter();
							VisualMappingManager manager = adapter.getVisualMappingManager();
							VisualStyle defaultStyle = manager.getDefaultVisualStyle();
							//VisualStyleFactory vsf = adapter.getVisualStyleFactory();
							manager.addVisualStyle(defaultStyle);
							manager.setCurrentVisualStyle(defaultStyle);
							defaultStyle.apply(childNetworkView);
							
							CyLayoutAlgorithmManager clam = adapter.getCyLayoutAlgorithmManager();
							CyLayoutAlgorithm alg = clam.getDefaultLayout();
							TaskIterator ti = alg.createTaskIterator(
									childNetworkView,alg.getDefaultLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);
							
							activator.getTaskManager().execute(ti);
							childNetworkView.updateView();
							
							//double scale = childNetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR).doubleValue() * 0.2;
			                //childNetworkView.setVisualProperty(BasicVisualLexicon.NETWORK_SCALE_FACTOR, scale);
			                
							childNetworkView.fitContent();
			                childNetworkView.updateView();
														
							return childNetworkView;
						}
					};
					
					worker.execute();
				}
			}
		}
		
		private void deselectAllNodesAndEdges(CyNetwork network) {
			List<CyNode> allNodes = network.getNodeList();
			for (CyNode node : allNodes) {
				network.getRow(node).set(CyNetwork.SELECTED, false);
			}
			List<CyEdge> allEdges = network.getEdgeList();
			for (CyEdge edge : allEdges) {
				network.getRow(edge).set(CyNetwork.SELECTED, false);
			}
		}
		
		private void selectNodesOrEdges(Collection<? extends CyIdentifiable> elements, CyNetwork network) {
			//final Collection<CyIdentifiable> allElements = new ArrayList<CyIdentifiable>(network.getNodeList());
			//allElements.addAll(network.getEdgeList());

			for (final CyIdentifiable nodeOrEdge : elements) {
				//boolean select = elements.contains(nodeOrEdge);
				network.getRow(nodeOrEdge).set(CyNetwork.SELECTED, true);
				CyNetworkViewManager cnvm = activator.getCyNetworkViewManager();
				Set<CyNetworkView> nvs = cnvm.getNetworkViewSet();
				Iterator<CyNetworkView> it = nvs.iterator();
				while (it.hasNext()) {
					CyNetworkView nv = it.next();
					nv.updateView();
				}
			}
		}
	}
	
	/**
	 * Handles the Save press for this dialog (save results to a file)
	 */
	private class SaveAction extends AbstractAction {
		private ArrayList complexes;
    	private CyNetwork network;
	    private int[] occurrences;

	    /**
	     * Save action constructor
	     *
	     * @param complexes Complexes to save
	     * @param network Network complexes are from for information about complex components
	     * @param occurrences Occurrences of matches
	     */
	    SaveAction(ArrayList complexes, CyNetwork network, int[] occurrences) {
	    	super("");
	    	this.complexes = complexes;
	    	this.network = network;
	    	this.occurrences = occurrences;
	    }

	    public void actionPerformed(ActionEvent e) {
	    	JFileChooser fc = new JFileChooser();
	    	int result = fc.showSaveDialog(new Component() {});
	    	if (result == JFileChooser.APPROVE_OPTION) {
	    		File file = fc.getSelectedFile();
	    		saveResults(complexes, network, occurrences, file.getAbsolutePath());
	    	}
    	}

	    /**
	     * Save results to a file
	     *
	     * @param complexes The list of complexes
	     * @param network The network source of the complexes
	     * @param occurrences Occurrences of matches
	     * @param fileName The file name to write to
	     * @return true if the file was written, false otherwise
	     */
	    public boolean saveResults(ArrayList complexes, CyNetwork network, int[] occurrences, String fileName) {
	    	if(complexes == null || network == null || fileName == null)
	    		return false;
	    	String lineSep = System.getProperty("line.separator");
	    	try {
	    		File file = new File(fileName);
	    		FileWriter fout = new FileWriter(file);
	    		fout.write(Common.APP_NAME + " Plugin Results" + lineSep);
	    		fout.write("Date: " + DateFormat.getDateTimeInstance().format(new Date()) + lineSep + lineSep);
	    		fout.write("Match Number\tOccurrences\tNames" + lineSep); //TODO node and edge count?
	    		ArrayList<ArrayList<String>> matches = getMatchesListFromComplexesList(complexes, network);
	    		Iterator<ArrayList<String>> iterator = matches.iterator();
	    		int i = 0;
	    		while(iterator.hasNext()) {
	    			ArrayList<String> match = iterator.next();
	    			fout.write((i + 1) + "\t\t");
	    			NumberFormat nf = NumberFormat.getInstance();
	    			nf.setMaximumFractionDigits(3);
	    			fout.write(occurrences[i] + "\t\t");
	    			fout.write(getNodeNameList(match).toString() + lineSep);
	    			i++;
	    		}
	    		fout.close();
	    		return true;
	    	}
	    	catch(IOException e) {
	    		JOptionPane.showMessageDialog(activator.getCySwingApplication().getJFrame(), e.toString(),
						"Error Writing to \"" + fileName + "\"", JOptionPane.ERROR_MESSAGE);
	    		return false;
	    	}
	    }
	}

	/**
	 * Handles the Close press for this dialog
	 */
	private class CloseAction extends AbstractAction {
		ResultsPanel p;

	    /**
	     * Close action constructor
	     *
	     * @param p The NetMatchResultsPanel handle
	     */
	    public CloseAction(ResultsPanel p) {
	    	super("");
	    	this.p = p;
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	CyServiceRegistrar csr = activator.getCyServiceRegistrar();
			
			CytoPanel resPanel = activator.getCySwingApplication().getCytoPanel(CytoPanelName.EAST);
			resPanel.setState(CytoPanelState.DOCK);
			csr.unregisterService(p, CytoPanelComponent.class);
	    	
	    	if (resPanel.getCytoPanelComponentCount() == 0)
	    		resPanel.setState(CytoPanelState.HIDE);
    	}
	}
	   
	/**
	 * A table cell rendered that centers the item in the cell
	 */
	private class CenterAndBoldRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setFont(new Font(this.getFont().getFontName(), Font.BOLD, 14));
			this.setHorizontalAlignment(CENTER);
			this.setVerticalAlignment(NORTH);
			return cell;
		}
	}
	
	/**
	 * A text area renderer that creates a line wrapped, non-editable text area
	 */
	private class JTextAreaRenderer extends JTextArea implements TableCellRenderer {

		/**
		 * Constructor
	     */
		public JTextAreaRenderer() {
			this.setLineWrap(true);
			this.setWrapStyleWord(true);
			this.setEditable(false);
			this.setAutoscrolls(true);
		}

	    /**
	     * Used to render a table cell.  Handles selection color and cell heigh and width.
	     * Note: Be careful changing this code as there could easily be infinite loops created
	     * when calculating preferred cell size as the user changes the dialog box size.
	     *
	     * @param table Parent table of cell
	     * @param value Value of cell
	     * @param isSelected True if cell is selected
	     * @param hasFocus True if cell has focus
	     * @param row The row of this cell
	     * @param column The column of this cell
	     * @return The cell to render by the calling code
	     */
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	StringBuffer sb = (StringBuffer)value;
	    	this.setText(sb.toString());
	    	if(isSelected) {
	    		this.setBackground(table.getSelectionBackground());
	        	this.setForeground(table.getSelectionForeground());
	    	}
	    	else {
	    		this.setBackground(table.getBackground());
	    		this.setForeground(table.getForeground());
	    	}
	    	//row height calculations
	    	int currentRowHeight = table.getRowHeight(row);
	    	this.setSize(table.getColumnModel().getColumn(column).getWidth(), currentRowHeight);
	    	int textAreaPreferredHeight = (int)this.getPreferredSize().getHeight();
	    	//JTextArea can grow and shrink here
	    	if(currentRowHeight < textAreaPreferredHeight)
	    		table.setRowHeight(row, textAreaPreferredHeight); //grow row height
	    	else if((currentRowHeight > textAreaPreferredHeight) && (currentRowHeight != defaultRowHeight)) {
	    		//defaultRowHeight check in if statement avoids infinite loop shrink row height
	    		table.setRowHeight(row, defaultRowHeight);
	    	}
	    	return this;
    	}
  	}
	
	private StringBuffer getNodeNameList(ArrayList<String> match) {
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
	
	private ArrayList<ArrayList<String>> getMatchesListFromComplexesList(ArrayList complexList, CyNetwork sourceNetwork) {
		ArrayList<ArrayList<String>> matchesList = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < complexList.size(); i++) {
			matchesList.add(getMatchesListFromComplexes((int[])complexList.get(i), sourceNetwork));
		}
		
		return matchesList;
	}
	
	private ArrayList<String> getMatchesListFromComplexes(int[] complex, CyNetwork sourceNetwork) {
		ArrayList<String> match = new ArrayList<String>();
		
		List<CyNode> nodeList = sourceNetwork.getNodeList();
		
		for (int i = 0; i < complex.length; i++) {
			CyNode node = nodeList.get(complex[i]);
			
			CyRow nodeRow = sourceNetwork.getRow(node);
			CyTable nodeTbl = sourceNetwork.getDefaultNetworkTable();
			
			ArrayList<CyColumn> columns = (ArrayList<CyColumn>) nodeTbl.getColumns();
			CyColumn attrCol = columns.get(1);
			String attrName = attrCol.getName();
			
			Class<?> type = attrCol.getType();
			String nodeValue = (String)nodeRow.get(attrName, type);
			
			match.add(nodeValue);
		}
		
		return match;
	}	
}
