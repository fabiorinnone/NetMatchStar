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
package netmatch.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import netmatch.exceptions.InvalidSIFException;
import netmatch.main.NetMatchHelp;
import netmatch.utils.*;
import netmatch.algorithm.significance.BarabasiAlbertTask;
import netmatch.algorithm.significance.DuplicationTask;
import netmatch.algorithm.significance.ErdosRenyiTask;
import netmatch.algorithm.significance.ForestFireTask;
import netmatch.algorithm.significance.GeometricTask;
import netmatch.algorithm.MatchTask;
import netmatch.algorithm.MetricsTask;
import netmatch.algorithm.significance.ShufflingTask;
import netmatch.algorithm.significance.WattsStrogatzTask;

import org.apache.commons.io.FilenameUtils;
import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.read.LoadNetworkFileTaskFactory;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.PanelTaskManager;

@SuppressWarnings("serial")
public class WestPanel extends JPanel implements CytoPanelComponent, ActionListener, ChangeListener {
	
	private static final int WIDTH_GENERAL = 450;
	private static final int WIDTH = 350;
	private static final int MINIMUM_WIDTH = 300;
	private static final int WEST_MIN_HEIGHT = 500;
	
	private static final String aboutImage = "bannerImage.jpg";
	private static final String selgimp = "select_gimp.gif";
	private static final String selmove = "select_move.gif";
	private static final String nodegimp = "node.gif";
	private static final String loopgimp = "loop_gimp.gif";
	private static final String edgegimp = "edge_gimp.gif";
	private static final String pathgimp = "path_gimp.gif";
	private static final String zoomin = "zoomin_gimp.gif";
	private static final String zoomout = "zoomout_gimp.gif";
	private static final String bi_fan = "bi_fan.gif";
	private static final String bi_parallel = "bi_parallel.gif";
	private static final String feed_forward_loop = "feed_forward_loop.gif";
	private static final String three_chain = "three_chain.gif";
	private static final String nuovo = "new.gif";
	private static final String load = "load.gif";
	private static final String save = "save.gif";
	private static final String change = "change.gif";
	private static final String pass = "pass.gif";
	private static final String exit = "exit.gif";
	private static final String clear = "clear.gif";
	private static final String nuovo16 = "new16.gif";
	private static final String load16 = "load16.gif";
	private static final String save16 = "save16.gif";
	private static final String change16 = "change16.gif";
	private static final String pass16 = "pass16.gif";
	private static final String exit16 = "exit.gif";
	private static final String clear16 = "clear16.gif";
	private static final String draw16 = "draw16.gif";
	private static final String xedit16 = "xedit.png";
	private static final String h = "help.gif";
	private static final String a = "about.gif";
	private static final String wiz = "wizard.gif";
	private static final String mton_fan = "mton_fan.gif";
	private static final String quickHelp = "help.gif";
	private static final String about16 = "about16.gif";
	private static final String path = "/img/";
	private static final String createnet = "new.gif";
	private static final String loadnet = "load.gif";
	
	private static JTextArea log;
	
	public ImageIcon[] icons;
	public ImageIcon[] structures;
	public ImageIcon[] menu;
	
	@SuppressWarnings("rawtypes")
	private JComboBox query;
	@SuppressWarnings("rawtypes")
	private JComboBox target;
	private JCheckBox labeled;
	private JCheckBox directed;
	@SuppressWarnings("rawtypes")
	private JComboBox qea, tea, qna, tna;

	private static JSlider randomNetsSlider;
	
	private JTextField shufflingQ;
	private JCheckBox labelShufflingCheckBox;
	private JTextField wsProb;
	private JTextField baInitNodes;
	private JTextField gmDim;
	private JTextField ffmNumAmbass;
	private JTextField ffmProb;
	private JTextField dmInitNodes;
	private JTextField dmProb;
	
	//public HashMap queries;
	
	@SuppressWarnings("rawtypes")
	public ArrayList listOfNodeAttributes;
	@SuppressWarnings("rawtypes")
	public ArrayList listOfEdgeAttributes;
	
	private HashMap<String,String> queryNetworkIdMap; 	//key: description, value = id
	private HashMap<String,String> targetNetworkIdMap;	//key: description, value = id
	
	private CySwingAppAdapter adapter = MenuAction.getAdapter();
	private CyNetworkManager cnm = adapter.getCyNetworkManager();
	
	@SuppressWarnings("rawtypes")
	private Set s = cnm.getNetworkSet();
	
    JAboutDialog aboutbox;
	NetMatchHelp help;
	JPanel matchingPanel;
	JPanel significancePanel;
	JPanel motifsLibraryPanel;
	
	JTabbedPane tabbedPane;
	
	private boolean isQueryApproximate;
	private boolean isQueryUnlabeled;
	//private Vector<String> approxPaths;
	//private TaskMonitor taskMonitor;
	private JButton[] sButtons;
	private JTextField seedField;
	private JCheckBox customSeed;
	private JRadioButton shufflingCheckBox;
	private JRadioButton erCheckBox;
	private JRadioButton wsCheckBox;
	private JRadioButton baCheckBox;
	private JRadioButton dmCheckBox;
	private JRadioButton gmCheckBox;
	private JRadioButton ffmCheckBox;
	
	private int seedValue;
	
	@SuppressWarnings("unused")
	private static String currentPath;
	
	@SuppressWarnings("rawtypes")
	public WestPanel(CySwingAppAdapter adapter){
		MyNetworkAddedListener netListen = new MyNetworkAddedListener(adapter);
		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		csr.registerService(netListen, NetworkAddedListener.class, new Properties());
		csr.registerService(netListen, NetworkAboutToBeDestroyedListener.class, new Properties());
		MyNetworkViewAddedListener netViewListen = new MyNetworkViewAddedListener(adapter);
		csr.registerService(netViewListen, NetworkViewAddedListener.class, new Properties());
		MyAddedNodesListener nodesListen = new MyAddedNodesListener(adapter);
		csr.registerService(nodesListen, AddedNodesListener.class, new Properties());
		MyAddedEdgesListener edgesListen = new MyAddedEdgesListener(adapter);
		csr.registerService(edgesListen, AddedEdgesListener.class, new Properties());
		MyColumnCreatedListener columnCreatedListen = new MyColumnCreatedListener(adapter);
		csr.registerService(columnCreatedListen, ColumnCreatedListener.class, new Properties());
		MyColumnDeletedListener columnDeletedListen = new MyColumnDeletedListener(adapter);
		csr.registerService(columnDeletedListen, ColumnDeletedListener.class, new Properties());
		
		help = null;
        aboutbox = null;
        setLayout(new BorderLayout());
        icons = new ImageIcon[9];
        icons[0] = new ImageIcon(getClass().getResource(path + aboutImage));
        icons[1] = new ImageIcon(getClass().getResource(path + selgimp));
        icons[2] = new ImageIcon(getClass().getResource(path + selmove));
        icons[3] = new ImageIcon(getClass().getResource(path + nodegimp));
        icons[4] = new ImageIcon(getClass().getResource(path + loopgimp));
        icons[5] = new ImageIcon(getClass().getResource(path + edgegimp));
        icons[6] = new ImageIcon(getClass().getResource(path + pathgimp));
        icons[7] = new ImageIcon(getClass().getResource(path + zoomin));
        icons[8] = new ImageIcon(getClass().getResource(path + zoomout));
        structures = new ImageIcon[5]; //5
        structures[0] = new ImageIcon(getClass().getResource(path + three_chain));
        structures[1] = new ImageIcon(getClass().getResource(path + feed_forward_loop));
        structures[2] = new ImageIcon(getClass().getResource(path + bi_parallel));
        structures[3] = new ImageIcon(getClass().getResource(path + bi_fan));
        structures[4] = new ImageIcon(getClass().getResource(path + mton_fan));
        menu = new ImageIcon[23];
        menu[0] = new ImageIcon(getClass().getResource(path + nuovo));
        menu[1] = new ImageIcon(getClass().getResource(path + load));
        menu[2] = new ImageIcon(getClass().getResource(path + save));
        menu[3] = new ImageIcon(getClass().getResource(path + change));
        menu[4] = new ImageIcon(getClass().getResource(path + pass));
        menu[5] = new ImageIcon(getClass().getResource(path + exit));
        menu[6] = new ImageIcon(getClass().getResource(path + clear));
        menu[7] = new ImageIcon(getClass().getResource(path + nuovo16));
        menu[8] = new ImageIcon(getClass().getResource(path + load16));
        menu[9] = new ImageIcon(getClass().getResource(path + save16));
        menu[10] = new ImageIcon(getClass().getResource(path + change16));
        menu[11] = new ImageIcon(getClass().getResource(path + pass16));
        menu[12] = new ImageIcon(getClass().getResource(path + exit16));
        menu[13] = new ImageIcon(getClass().getResource(path + clear16));
        menu[14] = new ImageIcon(getClass().getResource(path + draw16));
        menu[15] = new ImageIcon(getClass().getResource(path + xedit16));
        menu[16] = new ImageIcon(getClass().getResource(path + h));
        menu[17] = new ImageIcon(getClass().getResource(path + a));
        menu[18] = new ImageIcon(getClass().getResource(path + wiz));
        menu[19] = new ImageIcon(getClass().getResource(path + quickHelp));
        menu[20] = new ImageIcon(getClass().getResource(path + about16));
        menu[21] = new ImageIcon(getClass().getResource(path + createnet));
        menu[22] = new ImageIcon(getClass().getResource(path + loadnet));
        
        tabbedPane = new JTabbedPane();
        
        createMatchingPanel();
        createSignificancePanel();
        createMotifsLibraryPanel();
                        
        tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        
        add(tabbedPane);
        
        isQueryApproximate = false;
        isQueryUnlabeled = false;
        
        //approxPaths = new Vector<String>();
        
        queryNetworkIdMap = new HashMap<String, String>();
        targetNetworkIdMap = new HashMap<String, String>();
        
        Common.motifsMap = new HashMap<Long, Integer>();
        Common.mtonFanMap = new HashMap<Long, Pair<ArrayList<CyNode>>>();
        
        if (!s.isEmpty())
        	acquireData("");
	}
	
	private void createMatchingPanel() {
		matchingPanel = new JPanel();
		matchingPanel.setPreferredSize(new Dimension(WIDTH_GENERAL, WEST_MIN_HEIGHT));
		matchingPanel.setLayout(new BorderLayout());
		JToolBar menuBar = createMenuBar();
		matchingPanel.add(menuBar, BorderLayout.NORTH);        
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		JScrollPane scroller = new JScrollPane();
		scroller.getViewport().add(panel);
		
		//apertura
		JPanel panel4q = new JPanel();
		panel4q.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel4q.setMaximumSize(new Dimension(WIDTH,170));
		panel4q.setPreferredSize(new Dimension(WIDTH,170));
		panel4q.setMinimumSize(new Dimension(MINIMUM_WIDTH,170));
		panel4q.setLayout(new GridLayout(6,1,5,2));
		panel4q.setBorder(new TitledBorder(
				new EtchedBorder(), "Query Properties", TitledBorder.RIGHT, 
				TitledBorder.DEFAULT_JUSTIFICATION,null, Color.BLACK));
		
		JLabel lab = new JLabel("Query:");
		panel4q.add(lab);
		query = new JComboBox();
		query.setEditable(false);
		query.setEnabled(false);
		query.setBorder(new EtchedBorder());
		query.addActionListener(this);
		query.setToolTipText("Select a query.");
		panel4q.add(query);
		
		JLabel lab3 = new JLabel("Query Node Attributes:",JLabel.LEFT);
		panel4q.add(lab3);
		qna = new JComboBox();
		qna.setEnabled(false);
		qna.setToolTipText("<html>Select query node attributes."); 
		panel4q.add(qna);
		
		JLabel lab4 = new JLabel("Query Edge Attributes:",JLabel.LEFT);
		panel4q.add(lab4);
		qea = new JComboBox();
		qea.setEnabled(false);
		qea.setToolTipText("Select query edge attributes.");
		panel4q.add(qea);
		panel.add(panel4q);
		
		JPanel panel4t = new JPanel();
		panel4t.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel4t.setMaximumSize(new Dimension(WIDTH,170));
		panel4t.setPreferredSize(new Dimension(WIDTH,170));
		panel4t.setMinimumSize(new Dimension(MINIMUM_WIDTH,170));
		panel4t.setLayout(new GridLayout(6,1,5,2));
		panel4t.setBorder(new TitledBorder(
				new EtchedBorder(), "Target Network Properties", TitledBorder.RIGHT, 
				TitledBorder.DEFAULT_JUSTIFICATION, null, Color.BLACK));
		JLabel lab2 = new JLabel("Target Network:",JLabel.LEFT);
		panel4t.add(lab2);
		target = new JComboBox();
		target.setEditable(false);
		target.setEnabled(false);
		target.setBorder(new EtchedBorder());
		target.addActionListener(this);
		target.setToolTipText("Select a target network.");
		panel4t.add(target);
		JLabel lab6 = new JLabel("Target Network Node Attributes:",JLabel.LEFT);
		panel4t.add(lab6);
		tna = new JComboBox();
		tna.setEnabled(false);
		tna.addActionListener(this);
		tna.setToolTipText("<html>Select target network node attributes");
		panel4t.add(tna);
		JLabel lab66 = new JLabel("Target Network Edge Attributes:",JLabel.LEFT);
		panel4t.add(lab66);
		tea = new JComboBox();
		tea.setEnabled(false);
		tea.addActionListener(this);
		tea.setToolTipText("Select target network edge attributes.");
		panel4t.add(tea);
		panel.add(panel4t);
		
		JPanel panel7 = new JPanel(new GridLayout(1,1,2,2));
		panel7.setMaximumSize(new Dimension(WIDTH,70));
		panel7.setPreferredSize(new Dimension(WIDTH,70));
		panel7.setMinimumSize(new Dimension(MINIMUM_WIDTH,70));
		
		JPanel panel2 = new JPanel();
		panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.Y_AXIS));
		panel2.setBorder(new TitledBorder(
				new EtchedBorder(), "Graph Properties", TitledBorder.RIGHT, 
				TitledBorder.DEFAULT_JUSTIFICATION, null, Color.BLACK));
		labeled = new JCheckBox("Labeled");
		labeled.setSelected(true);
		labeled.addActionListener(this);
		labeled.setToolTipText("If not checked, all nodes and edges are supposed unlabeled.");
		panel2.add(labeled);
		panel2.add(Box.createHorizontalGlue());
		directed = new JCheckBox("Directed");
		directed.setSelected(true);
		directed.addActionListener(this);
		directed.setToolTipText("If not checked, all edges are supposed undirected.");
		panel2.add(directed);
		panel7.add(panel2);
		
		panel.add(panel7);
		
		JPanel panel5 = new JPanel();
		panel5.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel5.setMaximumSize(new Dimension(WIDTH, 35));
		panel5.setPreferredSize(new Dimension(WIDTH, 35));
		panel5.setMinimumSize(new Dimension(MINIMUM_WIDTH, 35));
		panel5.setLayout(new GridLayout(1,3,5,5));
		
		JButton go = new JButton("Match");
		go.setAlignmentX(Component.CENTER_ALIGNMENT);
		go.addActionListener(this);
		go.setToolTipText("Start matching.");
		panel5.add(go);
		JButton reset = new JButton("Reset");
		reset.setAlignmentX(Component.CENTER_ALIGNMENT);
		reset.addActionListener(this);
		reset.setToolTipText("Reset queries and networks.");
		panel5.add(reset);
		panel.add(panel5);
		
		JPanel panel6 = new JPanel(new BorderLayout());
		panel6.setAlignmentX(Component.CENTER_ALIGNMENT);
		log = new JTextArea();
		log.setEditable(false);
		log.setBackground(Color.WHITE);
		log.setBorder(new TitledBorder(
				new EtchedBorder(), "Log", TitledBorder.RIGHT, TitledBorder.DEFAULT_JUSTIFICATION, 
				null, Color.BLACK));
		JScrollPane scroller2 = new JScrollPane();
		scroller2.getViewport().add(log);
		panel6.add(scroller2,BorderLayout.CENTER);
		
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scroller,panel6);
		splitPane2.setOneTouchExpandable(true);
		splitPane2.setContinuousLayout(true);
		splitPane2.setDividerLocation(470);
		
		matchingPanel.add(splitPane2, BorderLayout.CENTER);
		
		//chiusura
		add(matchingPanel, BorderLayout.CENTER);
		
		tabbedPane.add("Matching", matchingPanel);
	}
	
	private void createSignificancePanel() {
		significancePanel = new JPanel();
		significancePanel.setPreferredSize(new Dimension(WIDTH_GENERAL, WEST_MIN_HEIGHT));
		significancePanel.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		JScrollPane scroller = new JScrollPane();
		scroller.getViewport().add(mainPanel);
		
		//OPTIONS START
		JPanel optionsPanel = new JPanel();
		optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.setMaximumSize(new Dimension(WIDTH, 95));
		optionsPanel.setPreferredSize(new Dimension(WIDTH, 95));
		optionsPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 95));
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBorder(new TitledBorder(
				new EtchedBorder(), "Options", TitledBorder.RIGHT, 
				TitledBorder.DEFAULT_JUSTIFICATION,null, Color.BLACK));
		
		JPanel randomNetsPanel = new JPanel();
		randomNetsPanel.setMaximumSize(new Dimension(WIDTH, 40));
		randomNetsPanel.setPreferredSize(new Dimension(WIDTH, 40));
		randomNetsPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 40));
		randomNetsPanel.setLayout(new GridLayout(1, 2, 2, 2));
		
		JLabel randomNetsLabel = new JLabel("Random networks", JLabel.LEFT);
		randomNetsLabel.setHorizontalAlignment(JLabel.LEFT);
		randomNetsLabel.setVerticalAlignment(JLabel.TOP);
		randomNetsLabel.setBorder(
				BorderFactory.createEmptyBorder(2, 2, 2, 2));
		randomNetsSlider = new JSlider(JSlider.HORIZONTAL);
		randomNetsSlider.setMinimum(0);
		randomNetsSlider.setMaximum(100);
		randomNetsSlider.setValue(50);
		randomNetsSlider.setEnabled(true);
		randomNetsSlider.setMajorTickSpacing(100);
		randomNetsSlider.setMinorTickSpacing(10);
		randomNetsSlider.setPaintTicks(true);
		randomNetsSlider.setPaintLabels(true);
		randomNetsSlider.setBorder(
				BorderFactory.createEmptyBorder(2, 2, 2, 2));
		//Font font = new Font("Serif", Font.ITALIC, 15);
		//randomNets.setFont(font);
		
		randomNetsSlider.addChangeListener(this);;
		
		randomNetsPanel.add(randomNetsLabel);
		randomNetsPanel.add(randomNetsSlider);
		
		optionsPanel.add(randomNetsPanel);
				
		JPanel seedPanel = new JPanel();
		seedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		seedPanel.setMaximumSize(new Dimension(WIDTH, 30));
		seedPanel.setPreferredSize(new Dimension(WIDTH, 30));
		seedPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 30));
		seedPanel.setLayout(new GridLayout(1, 2, 5, 2));
		
		JPanel leftSeedPanel = new JPanel();
		leftSeedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftSeedPanel.setMaximumSize(new Dimension(WIDTH, 30));
		leftSeedPanel.setPreferredSize(new Dimension(WIDTH, 30));
		leftSeedPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 30));
		leftSeedPanel.setLayout(new GridLayout(1, 1, 5, 2));
		
		customSeed = new JCheckBox("Custom seed", false);
		customSeed.addActionListener(this);
		customSeed.setActionCommand("Seed");
		leftSeedPanel.add(customSeed);
		
		seedPanel.add(leftSeedPanel);
		
		JPanel rightSeedPanel = new JPanel();
		rightSeedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		rightSeedPanel.setMaximumSize(new Dimension(WIDTH, 30));
		rightSeedPanel.setPreferredSize(new Dimension(WIDTH, 30));
		rightSeedPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 30));
		rightSeedPanel.setLayout(new GridLayout(1, 2, 5, 2));
		
		JLabel seedLabel = new JLabel("Rand seed");
		rightSeedPanel.add(seedLabel);
		
		Random random = new Random();
		Integer defaultSeed = random.nextInt(99999999);
		
		seedField = new JTextField(defaultSeed.toString());
		seedField.setEditable(false);
		seedField.setEnabled(false);
		rightSeedPanel.add(seedField);
		
		seedPanel.add(rightSeedPanel);
		
		optionsPanel.add(seedPanel);
		//OPTIONS END
		
		mainPanel.add(optionsPanel);
		
		//METRICS START
		JPanel metricsPanel = new JPanel();
		metricsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		metricsPanel.setMaximumSize(new Dimension(WIDTH, 60));
		metricsPanel.setPreferredSize(new Dimension(WIDTH, 60));
		metricsPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 60));
		metricsPanel.setLayout(new GridLayout(1, 3, 5, 2));
		metricsPanel.setBorder(new TitledBorder(
				new EtchedBorder(), "Metrics", TitledBorder.RIGHT, 
				TitledBorder.DEFAULT_JUSTIFICATION,null, Color.BLACK));
				
		JLabel metricsLabel = new JLabel("Compute metrics");
		metricsPanel.add(metricsLabel);
		
		metricsPanel.add(new JPanel());
		
		JButton metricsGo = new JButton("Start");
		metricsGo.setAlignmentX(Component.CENTER_ALIGNMENT);
		metricsGo.setActionCommand("Metrics");
		metricsGo.addActionListener(this);
		metricsPanel.add(metricsGo);
		//METRICS END
		
		mainPanel.add(metricsPanel);
		
		//MODELS START
		JPanel modelsPanel = new JPanel();
		modelsPanel.setMaximumSize(new Dimension(WIDTH, 350));
		modelsPanel.setPreferredSize(new Dimension(WIDTH, 350));
		modelsPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 350));
		modelsPanel.setLayout(new GridLayout(9, 3, 2, 2));
		modelsPanel.setBorder(new TitledBorder(
				new EtchedBorder(), "Models", TitledBorder.RIGHT, 
				TitledBorder.DEFAULT_JUSTIFICATION,null, Color.BLACK));
		
		JLabel modelLabel = new JLabel("Model", JLabel.LEFT);
		modelsPanel.add(modelLabel);
		
		modelsPanel.add(new JPanel());
		
		JLabel parametersLabel = new JLabel("Parameters", JLabel.RIGHT);
		modelsPanel.add(parametersLabel);
		
		shufflingCheckBox = new JRadioButton("Shuffling");
		shufflingCheckBox.setSelected(true);
		modelsPanel.add(shufflingCheckBox);
		
		labelShufflingCheckBox = new JCheckBox("Lab shuffling");
		labelShufflingCheckBox.addActionListener(this);
		labelShufflingCheckBox.setToolTipText("Shaffle vertex and edge labels");
		modelsPanel.add(labelShufflingCheckBox);
		
		JPanel swEdgPanel = new JPanel();
		swEdgPanel.setLayout(new GridLayout(1, 2));
		
		JLabel swEdgLabel = new JLabel("Sw/edg:");
		shufflingQ = new JTextField("100");
		shufflingQ.addActionListener(this);
		shufflingQ.setToolTipText("Number of switches per edge used to randomize a network. Default: 100");
		
		swEdgPanel.add(swEdgLabel);
		swEdgPanel.add(shufflingQ);
		
		modelsPanel.add(swEdgPanel);
		
		erCheckBox = new JRadioButton("Erdos-Renyi");
		modelsPanel.add(erCheckBox);
		
		modelsPanel.add(new JPanel());
		
		modelsPanel.add(new JPanel());
		
		wsCheckBox = new JRadioButton("Watts-Strogatz");
		modelsPanel.add(wsCheckBox);
		
		modelsPanel.add(new JPanel());
		
		JPanel wsPanel = new JPanel();
		wsPanel.setLayout(new GridLayout(1, 2));
		wsPanel.add(new JLabel("Rew prob:", JLabel.LEFT));
		wsProb = new JTextField("0.6");
		wsProb.setEditable(false);
		wsProb.setEnabled(false);
		wsProb.addActionListener(this);
		wsProb.setToolTipText("Rewiring probability (beta). Default: 0.6");
		wsPanel.add(wsProb);
		
		modelsPanel.add(wsPanel);
		
		baCheckBox = new JRadioButton("Barabasi-Albert");
		modelsPanel.add(baCheckBox);
		
		modelsPanel.add(new JPanel());
		
		JPanel baPanel = new JPanel();
		baPanel.setLayout(new GridLayout(1, 2));
		baPanel.add(new JLabel("Init nodes: ", JLabel.LEFT));
		baInitNodes = new JTextField("10");
		baInitNodes.setEditable(false);
		baInitNodes.setEnabled(false);
		baInitNodes.addActionListener(this);
		baInitNodes.setToolTipText("Number of initial nodes. Default: 10");
		baPanel.add(baInitNodes);
		
		modelsPanel.add(baPanel);
		
		dmCheckBox = new JRadioButton("Duplication");
		modelsPanel.add(dmCheckBox);
		
		JPanel dmPanel1 = new JPanel();
		dmPanel1.setLayout(new GridLayout(1, 2));
		dmPanel1.add(new JLabel("Init nodes:", JLabel.LEFT));
		dmInitNodes = new JTextField("15");
		dmInitNodes.setEditable(false);
		dmInitNodes.setEnabled(false);
		dmInitNodes.addActionListener(this);
		dmInitNodes.setToolTipText("Initial nodes number. Default: 2");
		dmPanel1.add(dmInitNodes);
		
		modelsPanel.add(dmPanel1);
		
		JPanel dmPanel2 = new JPanel();
		dmPanel2 .setLayout(new GridLayout(1, 2));
		dmPanel2.add(new JLabel("Edg prob: ", JLabel.LEFT));
		dmProb = new JTextField("0.7");
		dmProb.setEditable(false);
		dmProb.setEnabled(false);
		dmProb.addActionListener(this);
		dmProb.setToolTipText("Initial edge probability. Default: 0.7");
		dmPanel2.add(dmProb);
		
		modelsPanel.add(dmPanel2);
		
		gmCheckBox = new JRadioButton("Geometric");
		modelsPanel.add(gmCheckBox);
		
		modelsPanel.add(new JPanel());
		
		JPanel gmPanel = new JPanel();
		gmPanel.setLayout(new GridLayout(1, 2));
		gmPanel.add(new JLabel("Dim: ", JLabel.LEFT));
		gmDim = new JTextField("2");
		gmDim.setEditable(false);
		gmDim.setEnabled(false);
		gmDim.addActionListener(this);
		gmDim.setToolTipText("Dimensions. Default: 10");
		gmPanel.add(gmDim);
		
		modelsPanel.add(gmPanel);
		
		ffmCheckBox = new JRadioButton("Forest-fire");
		modelsPanel.add(ffmCheckBox);
		
		modelsPanel.add(new JPanel());
		
		JPanel ffmPanel = new JPanel();
		ffmPanel.setLayout(new GridLayout(1, 2));
		ffmPanel.add(new JLabel("Ambass: ", JLabel.LEFT));
		ffmNumAmbass = new JTextField("2");
		ffmNumAmbass.setEditable(false);
		ffmNumAmbass.setEnabled(false);
		ffmNumAmbass.setToolTipText("Number of ambassadors. Default: 2");
		ffmPanel.add(ffmNumAmbass);
		
		modelsPanel.add(ffmPanel);
		
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new GridLayout(1, 2));
		startPanel.setMaximumSize(new Dimension(WIDTH, 350));
		startPanel.setPreferredSize(new Dimension(WIDTH, 350));
		startPanel.setMinimumSize(new Dimension(MINIMUM_WIDTH, 350));
		
		modelsPanel.add(new JPanel());
		
		modelsPanel.add(new JPanel());
		
		JButton start = new JButton("Start");
		start.addActionListener(this);
		start.setActionCommand("Motif");
		
		ButtonGroup group = new ButtonGroup();
		group.add(shufflingCheckBox);
		group.add(erCheckBox);
		group.add(wsCheckBox);
		group.add(baCheckBox);
		group.add(dmCheckBox);
		group.add(gmCheckBox);
		group.add(ffmCheckBox);
		
		shufflingCheckBox.setActionCommand("Select shuffling");
		erCheckBox.setActionCommand("Select Erdos-Renyi");
		wsCheckBox.setActionCommand("Select Watts-Strogatz");
		baCheckBox.setActionCommand("Select Barabasi-Albert");
		dmCheckBox.setActionCommand("Select duplication");
		gmCheckBox.setActionCommand("Select geometric");
		ffmCheckBox.setActionCommand("Select Forest-fire");
		
		shufflingCheckBox.addActionListener(this);
		erCheckBox.addActionListener(this);
		wsCheckBox.addActionListener(this);
		baCheckBox.addActionListener(this);
		dmCheckBox.addActionListener(this);
		gmCheckBox.addActionListener(this);
		ffmCheckBox.addActionListener(this);
		
		modelsPanel.add(start);
		//MODELS END
		
		mainPanel.add(modelsPanel);
		
		significancePanel.add(scroller, BorderLayout.CENTER);
		
		//JScrollPane scroller = new JScrollPane(significancePanel);
		
		tabbedPane.add("Significance", significancePanel);
	}
	
	private void createMotifsLibraryPanel() {
		motifsLibraryPanel = new JPanel();
		motifsLibraryPanel.setPreferredSize(new Dimension(WIDTH_GENERAL, WEST_MIN_HEIGHT));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		JScrollPane scroller = new JScrollPane();
		scroller.getViewport().add(panel);
		
		
		JPanel panel1 = new JPanel();
		int diff = 40;
		panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel1.setMaximumSize(new Dimension(WIDTH - diff, WEST_MIN_HEIGHT));
		panel1.setPreferredSize(new Dimension(WIDTH - diff, WEST_MIN_HEIGHT));
		panel1.setMinimumSize(new Dimension(MINIMUM_WIDTH - diff, WEST_MIN_HEIGHT));
		
		GridLayout strLayout = new GridLayout(structures.length, 1);
		strLayout.setVgap(4);
		panel1.setLayout(strLayout);
		
		ButtonListener bListener = new ButtonListener();
		
		sButtons = new JButton[5];
		
		sButtons[0] = new JButton(structures[0]);
		sButtons[0].setName("TreeChain");
		sButtons[0].setToolTipText("Three Chain");
		sButtons[0].addActionListener(bListener);
		panel1.add(sButtons[0]);
		
		sButtons[1] = new JButton(structures[1]);
		sButtons[1].setName("FeedForwardLoop");
		sButtons[1].setToolTipText("Feed Forward Loop");
		sButtons[1].addActionListener(bListener);
		panel1.add(sButtons[1]);
		
		sButtons[2] = new JButton(structures[2]);
		sButtons[2].setName("Bi-Parallel");
		sButtons[2].setToolTipText("Bi-Parallel");
		sButtons[2].addActionListener(bListener);
		panel1.add(sButtons[2]);
		
		sButtons[3] = new JButton(structures[3]);
		sButtons[3].setName("Bi-Fan");
		sButtons[3].setToolTipText("Bi-Fan");
		sButtons[3].addActionListener(bListener);
		panel1.add(sButtons[3]);
		
		sButtons[4] = new JButton(structures[4]);
		sButtons[4].setName("MtoN-Fan");
		sButtons[4].setToolTipText("m to n");
		sButtons[4].addActionListener(bListener);
		panel1.add(sButtons[4]);
		
		panel1.setBorder(new EmptyBorder(4, 4, 4, 4));
		
		panel.add(panel1, BorderLayout.CENTER);
		
		motifsLibraryPanel.add(scroller, BorderLayout.CENTER);
		
		//JScrollPane scroller = new JScrollPane(motifsLibraryPanel);
		
		tabbedPane.add("Motifs library", motifsLibraryPanel);
	}
		
	public static JSlider getRandomNets() {
		return randomNetsSlider;
	}

	private JToolBar createMenuBar(){
		JToolBar toolBar = new JToolBar();
		JButton i;
	    i = new JButton(menu[12]);
		i.setToolTipText("Close NetMatch*");
		i.addActionListener(this);
		i.setActionCommand("Exit");
		toolBar.add(i);
		toolBar.addSeparator();
		
		i = new JButton(menu[21]);
		i.setToolTipText("Create New Query Network");
		i.addActionListener(this);
		i.setActionCommand("Create New Query Network");
		toolBar.add(i);
		//toolBar.addSeparator();
		
		i = new JButton(menu[22]);
		i.setToolTipText("Load New Network");
		i.addActionListener(this);
		i.setActionCommand("Load New Network");
		toolBar.add(i);
		//toolBar.addSeparator();

		i = new JButton(menu[2]);
		i.setToolTipText("Save Query Network");
		i.addActionListener(this);
		i.setActionCommand("Save Query Network");
		toolBar.add(i);
		toolBar.addSeparator();

	    i = new JButton(menu[17]);
		i.setToolTipText("About...");
		i.addActionListener(this);
		i.setActionCommand("About...");
		toolBar.add(i);
	    
		i = new JButton(menu[16]);
		i.setToolTipText("Documentation");
		i.addActionListener(this);
		i.setActionCommand("Documentation");
		toolBar.add(i);
		
		return toolBar;
	}

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Icon getIcon() {
		return menu[20];
	}

	@Override
	public String getTitle() {
		return "NetMatch*";
	}
	
	public static JTextArea getLog() {
		return log;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
	    Object o = e.getSource();
	    if(o == tna) {
	        if(((JComboBox)o).getSelectedItem().toString().equals(Strings.LIST_ATTRIBUTES) ||
	        		((JComboBox)o).getSelectedItem().toString().equals(Strings.LIST_ATTRIBUTES_CHANGED)) {
	        	MultipleAttrChoice m = new MultipleAttrChoice(this,"Multiple Node Attributes",((JComboBox)o),true);
	        	m.setVisible(true);
	        }
	        else {
	        	listOfNodeAttributes = new ArrayList();
	        	listOfNodeAttributes.add(tna.getSelectedItem());
	        }
	    }
	    else if(o == tea) {
	    	if(((JComboBox)o).getSelectedItem().toString().equals(Strings.LIST_ATTRIBUTES) ||
	    			((JComboBox)o).getSelectedItem().toString().equals(Strings.LIST_ATTRIBUTES_CHANGED)) {
	    		MultipleAttrChoice m = new MultipleAttrChoice(this,"Multiple Edge Attributes",((JComboBox)o),false);
	    		m.setVisible(true);
	    	}
	        else {
	        	listOfEdgeAttributes = new ArrayList();
	        	listOfEdgeAttributes.add(tea.getSelectedItem());
	        }
	    }
	    if (command.equals("Seed")) {
	    	if (customSeed.isSelected()) {
	    		seedField.setEditable(true);
	    		seedField.setEnabled(true);
	    	}
	    	else {
	    		seedField.setEditable(false);
	    		seedField.setEnabled(false);
	    	}
	    }
	    else if(command.equals("Select shuffling")) {
	    	this.labelShufflingCheckBox.setEnabled(true);
	    	this.shufflingQ.setEditable(true);
	    	this.shufflingQ.setEnabled(true);
	    	
	    	this.wsProb.setEditable(false);
	    	this.wsProb.setEnabled(false);
	    	
	    	this.baInitNodes.setEditable(false);
	    	this.baInitNodes.setEnabled(false);
	    	
	    	this.dmInitNodes.setEditable(false);
	    	this.dmInitNodes.setEnabled(false);
	    	this.dmProb.setEditable(false);
	    	this.dmProb.setEnabled(false);
	    	
	    	this.gmDim.setEditable(false);
	    	this.gmDim.setEnabled(false);
	    	
	    	this.ffmNumAmbass.setEditable(false);
	    	this.ffmNumAmbass.setEnabled(false);
	    }
	    else if(command.equals("Select Erdos-Renyi")) {
	    	this.labelShufflingCheckBox.setEnabled(false);
	    	this.shufflingQ.setEditable(false);
	    	this.shufflingQ.setEnabled(false);
	    	
	    	this.wsProb.setEditable(false);
	    	this.wsProb.setEnabled(false);
	    	
	    	this.baInitNodes.setEditable(false);
	    	this.baInitNodes.setEnabled(false);
	    	
	    	this.dmInitNodes.setEditable(false);
	    	this.dmInitNodes.setEnabled(false);
	    	this.dmProb.setEditable(false);
	    	this.dmProb.setEnabled(false);
	    	
	    	this.gmDim.setEditable(false);
	    	this.gmDim.setEnabled(false);
	    	
	    	this.ffmNumAmbass.setEditable(false);
	    	this.ffmNumAmbass.setEnabled(false);
	    	
	    }
	    else if(command.equals("Select Watts-Strogatz")) {
	    	this.labelShufflingCheckBox.setEnabled(true);
	    	this.shufflingQ.setEditable(false);
	    	this.shufflingQ.setEnabled(false);
	    	
	    	this.wsProb.setEditable(true);
	    	this.wsProb.setEnabled(true);
	    	
	    	this.baInitNodes.setEditable(false);
	    	this.baInitNodes.setEnabled(false);
	    	
	    	this.dmInitNodes.setEditable(false);
	    	this.dmInitNodes.setEnabled(false);
	    	this.dmProb.setEditable(false);
	    	this.dmProb.setEnabled(false);
	    	
	    	this.gmDim.setEditable(false);
	    	this.gmDim.setEnabled(false);
	    	
	    	this.ffmNumAmbass.setEditable(false);
	    	this.ffmNumAmbass.setEnabled(false);
	    	
	    }
	    else if(command.equals("Select Barabasi-Albert")) {
	    	this.labelShufflingCheckBox.setEnabled(false);
	    	this.shufflingQ.setEditable(false);
	    	this.shufflingQ.setEnabled(false);
	    	
	    	this.wsProb.setEditable(false);
	    	this.wsProb.setEnabled(false);
	    	
	    	this.baInitNodes.setEditable(true);
	    	this.baInitNodes.setEnabled(true);
	    	
	    	this.dmInitNodes.setEditable(false);
	    	this.dmInitNodes.setEnabled(false);
	    	this.dmProb.setEditable(false);
	    	this.dmProb.setEnabled(false);
	    	
	    	this.gmDim.setEditable(false);
	    	this.gmDim.setEnabled(false);
	    	
	    	this.ffmNumAmbass.setEditable(false);
	    	this.ffmNumAmbass.setEnabled(false);
	    	
	    }
	    else if(command.equals("Select duplication")) {
	    	this.labelShufflingCheckBox.setEnabled(false);
	    	this.shufflingQ.setEditable(false);
	    	this.shufflingQ.setEnabled(false);
	    	
	    	this.wsProb.setEditable(false);
	    	this.wsProb.setEnabled(false);
	    	
	    	this.baInitNodes.setEditable(false);
	    	this.baInitNodes.setEnabled(false);
	    	
	    	this.dmInitNodes.setEditable(true);
	    	this.dmInitNodes.setEnabled(true);
	    	this.dmProb.setEditable(true);
	    	this.dmProb.setEnabled(true);
	    	
	    	this.gmDim.setEditable(false);
	    	this.gmDim.setEnabled(false);
	    	
	    	this.ffmNumAmbass.setEditable(false);
	    	this.ffmNumAmbass.setEnabled(false);
	    	
	    }
	    else if(command.equals("Select geometric")) {
	    	this.labelShufflingCheckBox.setEnabled(false);
	    	this.shufflingQ.setEditable(false);
	    	this.shufflingQ.setEnabled(false);
	    	
	    	this.wsProb.setEditable(false);
	    	this.wsProb.setEnabled(false);
	    	
	    	this.baInitNodes.setEditable(false);
	    	this.baInitNodes.setEnabled(false);
	    	
	    	this.dmInitNodes.setEditable(false);
	    	this.dmInitNodes.setEnabled(false);
	    	this.dmProb.setEditable(false);
	    	this.dmProb.setEnabled(false);
	    	
	    	this.gmDim.setEditable(true);
	    	this.gmDim.setEnabled(true);
	    	
	    	this.ffmNumAmbass.setEditable(false);
	    	this.ffmNumAmbass.setEnabled(false);
	    	
	    }
	    else if(command.equals("Select Forest-fire")) {
	    	this.labelShufflingCheckBox.setEnabled(false);
	    	this.shufflingQ.setEditable(false);
	    	this.shufflingQ.setEnabled(false);
	    	
	    	this.wsProb.setEditable(false);
	    	this.wsProb.setEnabled(false);
	    	
	    	this.baInitNodes.setEditable(false);
	    	this.baInitNodes.setEnabled(false);
	    	
	    	this.dmInitNodes.setEditable(false);
	    	this.dmInitNodes.setEnabled(false);
	    	this.dmProb.setEditable(false);
	    	this.dmProb.setEnabled(false);
	    	
	    	this.gmDim.setEditable(false);
	    	this.gmDim.setEnabled(false);
	    	
	    	this.ffmNumAmbass.setEditable(true);
	    	this.ffmNumAmbass.setEnabled(true);
	    	
	    }
	    else if(command.equals("About...")) {
			aboutbox = new JAboutDialog(this, icons[0]);
		    aboutbox.setVisible(true);
		}
		else if(command.equals("Documentation")) {
			help = new NetMatchHelp("NetMatch* Help", menu[20]);
			help.setVisible(true);
		}
		else if(command.equals("Exit"))
			try {
				close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else if (command.equals("Create New Query Network")) {
			CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			CyNetworkFactory netFact = csr.getService(CyNetworkFactory.class);
			CyNetwork cyNetwork = netFact.createNetwork();
			cyNetwork.getRow(cyNetwork).set(CyNetwork.NAME,"QueryNetwork"+"-unamed-"+Common.indexN);
			
			CyTable nodeTable = cyNetwork.getDefaultNodeTable();
			//String columnName = Common.NODE_QUERY_ATTR;
			nodeTable.createColumn(Common.NODE_QUERY_ATTR, String.class, true, "?"); //immutable
			
			CyTable edgeTable = cyNetwork.getDefaultEdgeTable();
			edgeTable.createColumn(Common.EDGE_QUERY_ATTR, String.class, true, "?"); //immutable
			
			cnm.addNetwork(cyNetwork);
			
			CyNetworkViewFactory cnvf = adapter.getCyNetworkViewFactory();
			CyNetworkView cyView = cnvf.createNetworkView(cyNetwork);
			CyNetworkViewManager cnvm = adapter.getCyNetworkViewManager();
			cnvm.addNetworkView(cyView);
			
			cyView.updateView();
			
			Common.indexN++;
		}
		else if (command.equals("Load New Network")) {
			FileUtil fileUtil = adapter.getCyServiceRegistrar().getService(FileUtil.class);
			ArrayList filters = new ArrayList<FileChooserFilter>();
	    	filters.add(new FileChooserFilter("SIF File", "sif"));
	    	filters.add(new FileChooserFilter("OWL File", "owl"));
	    	filters.add(new FileChooserFilter("RDF File", "rdf"));
	    	filters.add(new FileChooserFilter("XML File", "xml"));
	    	filters.add(new FileChooserFilter("XGMML File", "xgmml"));
	    	filters.add(new FileChooserFilter("TSV File", "tsv"));
	    	filters.add(new FileChooserFilter("CSV File", "csv"));
	    	filters.add(new FileChooserFilter("TXT File", "txt"));
	    	filters.add(new FileChooserFilter("CYJS File", "cyjs"));
	    	filters.add(new FileChooserFilter("JSON File", "json"));
	    	filters.add(new FileChooserFilter("XLS File", "xls"));
	    	filters.add(new FileChooserFilter("XLSX File", "xlsx"));
	    	filters.add(new FileChooserFilter("GML File", "gml"));
	    	filters.add(new FileChooserFilter("Graphml File", "graphml"));
	    	filters.add(new FileChooserFilter("NNF File", "nnf"));
	    	filters.add(new FileChooserFilter("OBO File", "obo"));
	    	filters.add(new FileChooserFilter("GFF File", "gff"));
	    	
	    	// load file
	    	LoadNetworkFileTaskFactory loadFactory = adapter.get_LoadNetworkFileTaskFactory();
	    	File file = fileUtil.getFile(adapter.getCySwingApplication().getJFrame(), 
	    			"Load Dynamic Network", FileUtil.LOAD, filters);
	    	
	    	currentPath = file.getParent();
	    	
	    	/*VisualMappingManager manager = adapter.getVisualMappingManager();
	    	manager.setCurrentVisualStyle(manager.getDefaultVisualStyle());*/
	    	
	    	if (FilenameUtils.getExtension(file.getName()).equals("gff"))
	    		loadGFFFile(file);
	    	else {
	    		// dynamic viewer
	    		TaskIterator iterator = new TaskIterator(loadFactory.createTaskIterator(file).next());
	    		adapter.getTaskManager().execute(iterator);
	    	}
		}
		else if(command.equals("Save Query Network")) {
			FileUtil fileUtil = adapter.getCyServiceRegistrar().getService(FileUtil.class);
			ArrayList filters = new ArrayList<FileChooserFilter>();
	    	filters.add(new FileChooserFilter("sif", "sif"));
	    	saveQueryNetwork();
		}
		else if(command.equals("Reset")) {
			log.setText("");
			query.removeActionListener(this);
			target.removeActionListener(this);
			tea.removeActionListener(this);
			tna.removeActionListener(this);
			query.removeAllItems();
			query.setEnabled(false);
			target.removeAllItems();
			target.setEnabled(false);
			qea.removeAllItems();
			qea.setEnabled(false);
			qna.removeAllItems();
			qna.setEnabled(false);
			tea.removeAllItems();
			tea.setEnabled(false);
			tna.removeAllItems();
			tna.setEnabled(false);
			//result.clear();
			query.addActionListener(this);
			target.addActionListener(this);
			tea.addActionListener(this);
			tna.addActionListener(this);
			labeled.setSelected(true);
			directed.setSelected(true);
			//queries.clear();
			listOfNodeAttributes = null;
			listOfEdgeAttributes = null;
			System.gc();
			if(!s.isEmpty())
				acquireData("");
		    }
		else if (command.equals("Metrics")) {
			if(!query.isEnabled() || !target.isEnabled()) {
				JOptionPane.showMessageDialog(
						adapter.getCySwingApplication().getJFrame(), 
						"Please Select a Network and a Query First!", 
						"NetMatch*", 
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			else {
				String t = target.getSelectedItem().toString();
		        String q = query.getSelectedItem().toString();  
		        
		        String targetId = this.queryNetworkIdMap.get(t);
		        String queryId = this.targetNetworkIdMap.get(q);
		        
		        boolean isUsingQE = false; //TODO il query editor non è più presente
		        
		        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
		        String nn = queryId;
		        CyNetwork qnetwork = cnm.getNetwork(Integer.parseInt(nn));
		        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
		        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
		        	JOptionPane.showMessageDialog(
		        			adapter.getCySwingApplication().getJFrame(), 
		        			"Please Select a Network and Query (not empty) First!", 
		        			"NetMatch* Error", 
		        			JOptionPane.ERROR_MESSAGE);
		        else {
		        	Common.LABELED = labeled.isSelected();
		        	Common.DIRECTED = directed.isSelected();
		        	boolean direct = Common.DIRECTED;
		        	
		        	int qShufflingVal = Integer.parseInt(shufflingQ.getText());
					boolean labShuffling = Common.LABELED && this.labelShufflingCheckBox.isSelected();
					double wsProbVal = Double.parseDouble(wsProb.getText());
					int baInitNodesVal = Integer.parseInt(baInitNodes.getText());
					int gmDimVal = Integer.parseInt(gmDim.getText());
					int ffmNumAmbassVal = Integer.parseInt(ffmNumAmbass.getText());
					int dmInitVal = Integer.parseInt(dmInitNodes.getText());
		        	double dmProbVal = Double.parseDouble(dmProb.getText());
					
		        	log.setText("Computing metrics...\n");
		          
		        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
		        	
		        	String queryEdgeAttribute = (String)qea.getSelectedItem();
		        	String queryNodeAttribute = (String)qna.getSelectedItem();
				  
		        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
		        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
  				  
		        	TaskIterator taskIterator = new TaskIterator();
				  
		        	MetricsTask metricsTask;
		        	if (!isQueryUnlabeled)
		        		metricsTask = new MetricsTask(qShufflingVal, labShuffling, wsProbVal, 
		        				baInitNodesVal, gmDimVal, ffmNumAmbassVal, dmInitVal, dmProbVal,
		        				direct, tnetwork, qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
		        				queryEdgeAttribute, queryNodeAttribute, this, adapter);
		        	else {
		        		//approxPaths = getApproximatePaths(qnetwork, queryEdgeAttribute);
		        		metricsTask = new MetricsTask(qShufflingVal, labShuffling, wsProbVal, 
		        				baInitNodesVal, gmDimVal, ffmNumAmbassVal, dmInitVal, dmProbVal,
		        				direct, tnetwork, qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
		        				queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
		        				isQueryUnlabeled, this, adapter);
		        	}
				  
		        	taskIterator.append(metricsTask);
		        	dialogTaskManager.execute(taskIterator);
				  
        		}
			}
		}
		else if(command.equals("Motif")) {
			if (seedField.isEnabled()) {
				seedValue = Integer.parseInt(seedField.getText());
			}
			if(this.shufflingCheckBox.isSelected()) {
				if(!query.isEnabled() || !target.isEnabled()) {
					JOptionPane.showMessageDialog(
							adapter.getCySwingApplication().getJFrame(), 
							"Please Select a Network and a Query First!", 
							"NetMatch*", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else {
					//String t = (String)target.getSelectedItem();
			        //String q = (String)query.getSelectedItem();
			        String t = target.getSelectedItem().toString();
			        String q = query.getSelectedItem().toString();  
			        
			        String targetId = this.queryNetworkIdMap.get(t);
			        String queryId = this.targetNetworkIdMap.get(q);
			        
			        boolean isUsingQE = false;
			        //CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(t.substring(0, t.indexOf('-'))));
			        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
			        //tnetwork.getAdjacentEdgeIndicesArray();
			        //String nn = q.substring(0, q.indexOf('-')); 
			        String nn = queryId;
			        if(nn.equals("QE"))
			        	isUsingQE = true;
			        CyNetwork qnetwork = null;
			        if(!isUsingQE)
	  		          	qnetwork = cnm.getNetwork(Integer.parseInt(nn));
			        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
			        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
			        	JOptionPane.showMessageDialog(
			        			adapter.getCySwingApplication().getJFrame(), 
			        			"Please Select a Network and Query (not empty) First!", 
			        			"NetMatch* Error", 
			        			JOptionPane.ERROR_MESSAGE);
			        else {
			        	Common.LABELED = labeled.isSelected();
			        	Common.DIRECTED = directed.isSelected();
			        	int N = randomNetsSlider.getValue();
						int Q = Integer.parseInt(shufflingQ.getText());
						boolean direct = Common.DIRECTED;
						boolean labshuff = Common.LABELED && this.labelShufflingCheckBox.isSelected();
						
			        	log.setText("Start motif verification...\n");
			          
			        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
					  
			        	String queryEdgeAttribute = (String)qea.getSelectedItem();
			        	String queryNodeAttribute = (String)qna.getSelectedItem();
					  
			        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
			        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
	  				  
			        	TaskIterator taskIterator = new TaskIterator();
					  
			        	ShufflingTask shufflingTask;
			        	if (!isQueryUnlabeled) {
			        		shufflingTask = new ShufflingTask(Q, N, direct, labshuff, tnetwork, 
			        				qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        				queryEdgeAttribute, queryNodeAttribute, this, adapter);
			        	}
			        	else {
			        		shufflingTask = new ShufflingTask(Q, N, direct, labshuff, tnetwork, 
			        				qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        				queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        				isQueryUnlabeled, this, adapter);
			        	}
					  
			        	taskIterator.append(shufflingTask);
			        	dialogTaskManager.execute(taskIterator);
					  
	        		}
				}
			}
			if(this.erCheckBox.isSelected()) {
				//System.out.println("Starting Erdos-Renyi");
				if(!query.isEnabled() || !target.isEnabled()) {
					JOptionPane.showMessageDialog(
							adapter.getCySwingApplication().getJFrame(), 
							"Please Select a Network and a Query First!", 
							"NetMatch*", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else {
					String t = target.getSelectedItem().toString();
			        String q = query.getSelectedItem().toString();  
			        
			        String targetId = this.queryNetworkIdMap.get(t);
			        String queryId = this.targetNetworkIdMap.get(q);
			        
			        boolean isUsingQE = false; //TODO il query editor non è più presente
			        
			        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
			        String nn = queryId;
			        CyNetwork qnetwork = cnm.getNetwork(Integer.parseInt(nn));
			        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
			        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
			        	JOptionPane.showMessageDialog(
			        			adapter.getCySwingApplication().getJFrame(), 
			        			"Please Select a Network and Query (not empty) First!", 
			        			"NetMatch* Error", 
			        			JOptionPane.ERROR_MESSAGE);
			        else {
			        	Common.LABELED = labeled.isSelected();
			        	Common.DIRECTED = directed.isSelected();
			        	int n = randomNetsSlider.getValue();
						boolean direct = Common.DIRECTED;
						
			        	log.setText("Start motif verification...\n");
			          
			        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
					  
			        	String queryEdgeAttribute = (String)qea.getSelectedItem();
			        	String queryNodeAttribute = (String)qna.getSelectedItem();
					  
			        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
			        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
	  				  
			        	TaskIterator taskIterator = new TaskIterator();
					  
			        	ErdosRenyiTask erdosRenyiTask;
			        	if (!isQueryUnlabeled) {
			        		if (!seedField.isEnabled()) {
			        			erdosRenyiTask = new ErdosRenyiTask(n, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, this, adapter);
			        		}
			        		else {
			        			erdosRenyiTask = new ErdosRenyiTask(n, direct, tnetwork,
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes,
			        					queryEdgeAttribute, queryNodeAttribute, seedValue,
			        					this, adapter);
			        		}
			        	}
			        	else {
			        		if (!seedField.isEnabled()) {
			        		erdosRenyiTask = new ErdosRenyiTask(n, direct, tnetwork, 
			        				qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        				queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        				isQueryUnlabeled, this, adapter);
			        		}
			        		else {
			        			erdosRenyiTask = new ErdosRenyiTask(n, direct, tnetwork, 
				        				qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
				        				queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
				        				isQueryUnlabeled, seedValue, this, adapter);	
			        		}
			        	}
			        	taskIterator.append(erdosRenyiTask);
			        	dialogTaskManager.execute(taskIterator);
					  
	        		}
				}
			}
			if(this.wsCheckBox.isSelected()) {
				//System.out.println("Starting Watts-Strogatz");
				if(!query.isEnabled() || !target.isEnabled()) {
					JOptionPane.showMessageDialog(
							adapter.getCySwingApplication().getJFrame(), 
							"Please Select a Network and a Query First!", 
							"NetMatch*", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else {
					String t = target.getSelectedItem().toString();
			        String q = query.getSelectedItem().toString();  
			        
			        String targetId = this.queryNetworkIdMap.get(t);
			        String queryId = this.targetNetworkIdMap.get(q);
			        
			        boolean isUsingQE = false; //TODO il query editor non è più presente
			        
			        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
			        String nn = queryId;
			        CyNetwork qnetwork = cnm.getNetwork(Integer.parseInt(nn));
			        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
			        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
			        	JOptionPane.showMessageDialog(
			        			adapter.getCySwingApplication().getJFrame(), 
			        			"Please Select a Network and Query (not empty) First!", 
			        			"NetMatch* Error", 
			        			JOptionPane.ERROR_MESSAGE);
			        else {
			        	Common.LABELED = labeled.isSelected();
			        	Common.DIRECTED = directed.isSelected();
			        	int n = randomNetsSlider.getValue();
			        	double p = Double.parseDouble(wsProb.getText());
						boolean direct = Common.DIRECTED;
						
			        	log.setText("Start motif verification...\n");
			          
			        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
					  
			        	String queryEdgeAttribute = (String)qea.getSelectedItem();
			        	String queryNodeAttribute = (String)qna.getSelectedItem();
					  
			        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
			        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
	  				  
			        	TaskIterator taskIterator = new TaskIterator();
					  
			        	WattsStrogatzTask wattsStrogatzTask;
			        	if (!isQueryUnlabeled) {
			        		if (!seedField.isEnabled()) {
			        			wattsStrogatzTask = new WattsStrogatzTask(n, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, this, adapter);
			        		}
			        		else {
			        			wattsStrogatzTask = new WattsStrogatzTask(n, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, seedValue, this, adapter);
			        		}
			        	}
			        	else {
			        		if (!seedField.isEnabled()) {
			        			wattsStrogatzTask = new WattsStrogatzTask(n, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, this, adapter);
			        		}
			        		else {
			        			wattsStrogatzTask = new WattsStrogatzTask(n, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, seedValue, this, adapter);
			        		}
			        	}
					  
			        	taskIterator.append(wattsStrogatzTask);
			        	dialogTaskManager.execute(taskIterator);
					  
	        		}
				}
			}
			if(this.baCheckBox.isSelected()) {
				//System.out.println("Starting Barabasi-Albert");
				if(!query.isEnabled() || !target.isEnabled()) {
					JOptionPane.showMessageDialog(
							adapter.getCySwingApplication().getJFrame(), 
							"Please Select a Network and a Query First!", 
							"NetMatch*", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else {
					String t = target.getSelectedItem().toString();
			        String q = query.getSelectedItem().toString();  
			        
			        String targetId = this.queryNetworkIdMap.get(t);
			        String queryId = this.targetNetworkIdMap.get(q);
			        
			        boolean isUsingQE = false; //TODO il query editor non è più presente
			        
			        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
			        String nn = queryId;
			        CyNetwork qnetwork = cnm.getNetwork(Integer.parseInt(nn));
			        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
			        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
			        	JOptionPane.showMessageDialog(
			        			adapter.getCySwingApplication().getJFrame(), 
			        			"Please Select a Network and Query (not empty) First!", 
			        			"NetMatch* Error", 
			        			JOptionPane.ERROR_MESSAGE);
			        else {
			        	Common.LABELED = labeled.isSelected();
			        	Common.DIRECTED = directed.isSelected();
			        	int n = randomNetsSlider.getValue();
			        	int i = Integer.parseInt(baInitNodes.getText());
						boolean direct = Common.DIRECTED;
						
			        	log.setText("Start motif verification...\n");
			          
			        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
					  
			        	String queryEdgeAttribute = (String)qea.getSelectedItem();
			        	String queryNodeAttribute = (String)qna.getSelectedItem();
					  
			        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
			        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
	  				  
			        	TaskIterator taskIterator = new TaskIterator();
					  
			        	BarabasiAlbertTask barabasiAlbertTask;
			        	if (!isQueryUnlabeled) {
			        		if (!seedField.isEnabled()) {
			        			barabasiAlbertTask = new BarabasiAlbertTask(n, i, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, this, adapter);
			        		}
			        		else {
			        			barabasiAlbertTask = new BarabasiAlbertTask(n, i, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, seedValue, this, adapter);
			        		}
			        	}
			        	else {
			        		if (!seedField.isEnabled()) {
			        			barabasiAlbertTask = new BarabasiAlbertTask(n, i, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, this, adapter);
			        		}
			        		else {
			        			barabasiAlbertTask = new BarabasiAlbertTask(n, i, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, seedValue, this, adapter);
			        		}
			        	}
					  
			        	taskIterator.append(barabasiAlbertTask);
			        	dialogTaskManager.execute(taskIterator);
	        		}
				}
			}
			if(this.gmCheckBox.isSelected()) {
				if(!query.isEnabled() || !target.isEnabled()) {
					JOptionPane.showMessageDialog(
							adapter.getCySwingApplication().getJFrame(), 
							"Please Select a Network and a Query First!", 
							"NetMatch*", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else {
					String t = target.getSelectedItem().toString();
			        String q = query.getSelectedItem().toString();  
			        
			        String targetId = this.queryNetworkIdMap.get(t);
			        String queryId = this.targetNetworkIdMap.get(q);
			        
			        boolean isUsingQE = false; //TODO il query editor non è più presente
			        
			        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
			        String nn = queryId;
			        CyNetwork qnetwork = cnm.getNetwork(Integer.parseInt(nn));
			        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
			        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
			        	JOptionPane.showMessageDialog(
			        			adapter.getCySwingApplication().getJFrame(), 
			        			"Please Select a Network and Query (not empty) First!", 
			        			"NetMatch* Error", 
			        			JOptionPane.ERROR_MESSAGE);
			        else {
			        	Common.LABELED = labeled.isSelected();
			        	Common.DIRECTED = directed.isSelected();
			        	int n = randomNetsSlider.getValue();
			        	int d = Integer.parseInt(gmDim.getText());
						boolean direct = Common.DIRECTED;
						
			        	log.setText("Start motif verification...\n");
			          
			        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
					  
			        	String queryEdgeAttribute = (String)qea.getSelectedItem();
			        	String queryNodeAttribute = (String)qna.getSelectedItem();
					  
			        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
			        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
	  				  
			        	TaskIterator taskIterator = new TaskIterator();
					  
			        	GeometricTask geometricTask;
			        	if (!isQueryUnlabeled) {
			        		if (!seedField.isEnabled()) {
			        			geometricTask = new GeometricTask(n, d, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, this, adapter);
			        		}
			        		else {
			        			geometricTask = new GeometricTask(n, d, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, seedValue, this, adapter);
			        		}
			        	}
			        	else {
			        		if (!seedField.isEnabled()) {
			        			geometricTask = new GeometricTask(n, d, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, this, adapter);
			        		}
			        		else {
			        			geometricTask = new GeometricTask(n, d, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, seedValue, this, adapter);
			        		}
			        	}
					  
			        	taskIterator.append(geometricTask);
			        	dialogTaskManager.execute(taskIterator);
					  
	        		}
				}
			}
			if(this.ffmCheckBox.isSelected()) {
				if(!query.isEnabled() || !target.isEnabled()) {
					JOptionPane.showMessageDialog(
							adapter.getCySwingApplication().getJFrame(), 
							"Please Select a Network and a Query First!", 
							"NetMatch*", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else {
					String t = target.getSelectedItem().toString();
			        String q = query.getSelectedItem().toString();  
			        
			        String targetId = this.queryNetworkIdMap.get(t);
			        String queryId = this.targetNetworkIdMap.get(q);
			        
			        boolean isUsingQE = false; //TODO il query editor non è più presente
			        
			        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
			        String nn = queryId;
			        CyNetwork qnetwork = cnm.getNetwork(Integer.parseInt(nn));
			        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
			        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
			        	JOptionPane.showMessageDialog(
			        			adapter.getCySwingApplication().getJFrame(), 
			        			"Please Select a Network and Query (not empty) First!", 
			        			"NetMatch* Error", 
			        			JOptionPane.ERROR_MESSAGE);
			        else {
			        	Common.LABELED = labeled.isSelected();
			        	Common.DIRECTED = directed.isSelected();
			        	int n = randomNetsSlider.getValue();
			        	int a = Integer.parseInt(ffmNumAmbass.getText());
			        	boolean direct = Common.DIRECTED;
						
			        	log.setText("Start motif verification...\n");
			          
			        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
					  
			        	String queryEdgeAttribute = (String)qea.getSelectedItem();
			        	String queryNodeAttribute = (String)qna.getSelectedItem();
					  
			        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
			        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
	  				  
			        	TaskIterator taskIterator = new TaskIterator();
					  
			        	ForestFireTask forestFireTask;
			        	if (!isQueryUnlabeled) {
			        		if (!seedField.isEnabled()) {
			        			forestFireTask = new ForestFireTask(n, a, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, this, adapter);
			        		}
			        		else {
			        			forestFireTask = new ForestFireTask(n, a, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, seedValue, this, adapter);
			        		}
			        	}
			        	else {
			        		if (!seedField.isEnabled()) {
			        			forestFireTask = new ForestFireTask(n, a, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, this, adapter);
			        		}
			        		else {
			        			forestFireTask = new ForestFireTask(n, a, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, this, adapter);
			        		}
			        	}
					  
			        	taskIterator.append(forestFireTask);
			        	dialogTaskManager.execute(taskIterator);
					  
	        		}
				}
			}
			if(this.dmCheckBox.isSelected()) {
				if(!query.isEnabled() || !target.isEnabled()) {
					JOptionPane.showMessageDialog(
							adapter.getCySwingApplication().getJFrame(), 
							"Please Select a Network and a Query First!", 
							"NetMatch*", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else {
					String t = target.getSelectedItem().toString();
			        String q = query.getSelectedItem().toString();  
			        
			        String targetId = this.queryNetworkIdMap.get(t);
			        String queryId = this.targetNetworkIdMap.get(q);
			        
			        boolean isUsingQE = false; //TODO il query editor non è più presente
			        
			        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
			        String nn = queryId;
			        CyNetwork qnetwork = cnm.getNetwork(Integer.parseInt(nn));
			        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
			        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
			        	JOptionPane.showMessageDialog(
			        			adapter.getCySwingApplication().getJFrame(), 
			        			"Please Select a Network and Query (not empty) First!", 
			        			"NetMatch* Error", 
			        			JOptionPane.ERROR_MESSAGE);
			        else {
			        	Common.LABELED = labeled.isSelected();
			        	Common.DIRECTED = directed.isSelected();
			        	int n = randomNetsSlider.getValue();
			        	int i = Integer.parseInt(dmInitNodes.getText());
			        	double p = Double.parseDouble(dmProb.getText());
						boolean direct = Common.DIRECTED;
						
			        	log.setText("Start motif verification...\n");
			          
			        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
					  
			        	String queryEdgeAttribute = (String)qea.getSelectedItem();
			        	String queryNodeAttribute = (String)qna.getSelectedItem();
					  
			        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
			        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
	  				  
			        	TaskIterator taskIterator = new TaskIterator();
					  
			        	DuplicationTask duplicationTask;
			        	if (!isQueryUnlabeled) {
			        		if (!seedField.isEnabled()) {
			        			duplicationTask = new DuplicationTask(n, i, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, this, adapter);
			        		}
			        		else {
			        			duplicationTask = new DuplicationTask(n, i, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, seedValue, this, adapter);
			        		}
			        	}
			        	else {
			        		if (!seedField.isEnabled()) {
			        			duplicationTask = new DuplicationTask(n, i, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, this, adapter);
			        		}
			        		else {
			        			duplicationTask = new DuplicationTask(n, i, p, direct, tnetwork, 
			        					qnetwork, listOfEdgeAttributes, listOfNodeAttributes, 
			        					queryEdgeAttribute, queryNodeAttribute, isQueryApproximate, 
			        					isQueryUnlabeled, seedValue, this, adapter);
			        		}
			        	}
					  
			        	taskIterator.append(duplicationTask);
			        	dialogTaskManager.execute(taskIterator);
					  
	        		}
				}
			}
		}
		else if(command.equals("Match")) {
			if(!query.isEnabled() || !target.isEnabled()) {
				JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), 
						"Please Select a Network and a Query First!", 
						"NetMatch*", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				//String t = (String)target.getSelectedItem();
		        //String q = (String)query.getSelectedItem();
		        String t = target.getSelectedItem().toString();
		        String q = query.getSelectedItem().toString();  
		        
		        String targetId = this.queryNetworkIdMap.get(t);
		        String queryId = this.targetNetworkIdMap.get(q);
		        
		        boolean isUsingQE = false;
		        //CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(t.substring(0, t.indexOf('-'))));
		        CyNetwork tnetwork = cnm.getNetwork(Integer.parseInt(targetId));
		        //tnetwork.getAdjacentEdgeIndicesArray();
		        //String nn = q.substring(0, q.indexOf('-')); 
		        String nn = queryId;
		        if(nn.equals("QE"))
		        	isUsingQE = true;
		        CyNetwork qnetwork = null;
		        if(!isUsingQE)
  		          	qnetwork = cnm.getNetwork(Integer.parseInt(nn));
		        if(tnetwork == null || tnetwork.getNodeCount() < 1 || 
		        		(qnetwork == null && !isUsingQE) || (qnetwork != null && qnetwork.getNodeCount() < 1))
		        	JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(), 
		        			"Please Select a Network and Query (not empty) First!", 
		        			"NetMatch* Error", 
		        			JOptionPane.ERROR_MESSAGE);
		        else {
		        	Common.LABELED = labeled.isSelected();
		        	Common.DIRECTED = directed.isSelected();
		        	log.setText("Start Matching...\n");
		          
		        	CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
		        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
				  
		        	String queryEdgeAttribute = (String)qea.getSelectedItem();
		        	String queryNodeAttribute = (String)qna.getSelectedItem();
				  
		        	isQueryApproximate = isQueryApproximate(qnetwork, queryEdgeAttribute);
		        	isQueryUnlabeled = isQueryUnlabeled(qnetwork, queryNodeAttribute, queryEdgeAttribute);
  				  
		        	TaskIterator taskIterator = new TaskIterator();
				  
		        	MatchTask matchTask;
		        	if (!isQueryUnlabeled)
		        		matchTask = new MatchTask(tnetwork, qnetwork, listOfEdgeAttributes, 
		        				listOfNodeAttributes, queryEdgeAttribute, queryNodeAttribute, 
		        				this, adapter);
		        	else {
		        		//approxPaths = getApproximatePaths(qnetwork, queryEdgeAttribute);
		        		matchTask = new MatchTask(tnetwork, qnetwork, listOfEdgeAttributes, 
		        				listOfNodeAttributes, queryEdgeAttribute, queryNodeAttribute, 
		        				isQueryApproximate, isQueryUnlabeled, this, adapter);
		        	}
				  
		        	taskIterator.append(matchTask);
		        	dialogTaskManager.execute(taskIterator);
				  
        		}
			}
    	}
		else if(o == query) {
			Object oldQea = qea.getSelectedItem();
			Object oldQna = qna.getSelectedItem();
		        
			qea.removeAllItems();
			qna.removeAllItems();
		        
			String qu = (String)query.getSelectedItem().toString();
			int endqu=qu.indexOf('-');
			String nn;
			if (endqu==-1)
				nn = qu;
	        else
	        	nn = qu.substring(0, qu.indexOf('-'));
	        if(nn.equals("QE")) {
	        	qea.setEnabled(true);
	        	qna.setEnabled(true);
	        	qna.addItem(qu + " - Nodes Attributes");
	        	qea.addItem(qu + " - Edges Attributes");
	        }
	        else {
	        	CyNetwork network = cnm.getNetwork(Long.parseLong(((Item) query.getSelectedItem()).getId()));
	        	String networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
	        	
	        	CyTable nodeTbl = network.getDefaultNodeTable();
	        	CyTable edgeTbl = network.getDefaultEdgeTable();
	        	
	        	//String[] nodeAttrNames = CyTableUtil.getColumnNames(nodeTbl).toArray(new String[0]);
	        	//String[] edgeAttrNames = CyTableUtil.getColumnNames(edgeTbl).toArray(new String[0]);
	        	ArrayList<String> nodeAttrNames = new ArrayList<String>();
	        	ArrayList<String> edgeAttrNames = new ArrayList<String>();
	        	
	        	//ciclo sugli attributi dei nodi
	        	ArrayList<CyColumn> nodeColumns = (ArrayList<CyColumn>) nodeTbl.getColumns();
	        	Iterator<CyColumn> nodeColumnsIterator = nodeColumns.iterator();
	        	while(nodeColumnsIterator.hasNext()) {
	        		CyColumn nodeColumn = nodeColumnsIterator.next();
	        		
	        		String nodeColumnName = nodeColumn.getName();
	        		Class<?> type = nodeColumn.getType();
	        		
	        		if (networkName.startsWith("QueryNetwork")) {
	        			if (nodeColumnName.equals(Common.NODE_QUERY_ATTR))
	        				nodeAttrNames.add(nodeColumnName);
					}
					else {
						if (!nodeColumnName.equals(Strings.SUID) && !nodeColumnName.equals(Strings.SELECTED)) {
							if (!Collection.class.isAssignableFrom(type))
								nodeAttrNames.add(nodeColumnName);
						}
					}
				}
					
				//ciclo sugli attributi degli archi
	        	ArrayList<CyColumn> edgeColumns = (ArrayList<CyColumn>) edgeTbl.getColumns();
	        	Iterator<CyColumn> edgeColumnsIterator = edgeColumns.iterator();
	        	while(edgeColumnsIterator.hasNext()) {
	        		CyColumn edgeColumn = edgeColumnsIterator.next();
	        		
	        		String edgeColumnName = edgeColumn.getName();
	        		Class<?> type = edgeColumn.getType();
						
					if (networkName.startsWith("QueryNetwork")) {
						if (edgeColumnName.equals(Common.EDGE_QUERY_ATTR))
		            		  edgeAttrNames.add(edgeColumnName);
					}
        		  	else {
        		  		if (!edgeColumnName.equals(Strings.SUID) && !edgeColumnName.equals(Strings.SELECTED)) {
        		  			if (!Collection.class.isAssignableFrom(type))
        		  				edgeAttrNames.add(edgeColumnName);
        		  		}
        		  	}
				}
					
				Iterator<String> nodeAttrNamesIterator = nodeAttrNames.iterator();
				while (nodeAttrNamesIterator.hasNext()) {
					String nodeAttrName = nodeAttrNamesIterator.next();
					qna.addItem(nodeAttrName);
				}
				//qna.addItem(Strings.DEFAULT_ATTRIBUTES);
				
				Iterator<String> edgeAttrNamesIterator = edgeAttrNames.iterator();
				while (edgeAttrNamesIterator.hasNext()) {
					String edgeAttrName = edgeAttrNamesIterator.next();
					qea.addItem(edgeAttrName);
				}
	            
	            /*for(int i = 0;i < nodeAttrNames.length;i++)
	            	qna.addItem(nodeAttrNames[i]);
    	        qna.addItem(Strings.DEFAULT_ATTRIBUTES); 
	    	    for(int i = 0;i < edgeAttrNames.length;i++)
	    	    		qea.addItem(edgeAttrNames[i]);
	            }*/
	          	if(oldQea != null) {
	          		for(int i = 0;i < qea.getItemCount();i++) {
	          			Object o1 = qea.getItemAt(i);
	          			if(o1.equals(oldQea)) {
	          				qea.setSelectedItem(oldQea);
	          				break;
	          			}
	          		}
	          	}
	          	if(oldQna != null) {
	          		for(int i = 0;i < qna.getItemCount();i++) {
	          			Object o1 = qna.getItemAt(i);
	          			if(o1.equals(oldQna)) {
	          				qna.setSelectedItem(oldQna);
	          				break;
	          			}   
	          		}
	          	}
        	}
		}
		else if (o == target) {
			target.removeActionListener(this);
		    tea.removeActionListener(this);
		    tna.removeActionListener(this);

			Object oldTea = tea.getSelectedItem();
	        Object oldTna = tna.getSelectedItem();
	        
	        tea.removeAllItems();
	        tna.removeAllItems();
	        
	        CyNetwork network = cnm.getNetwork(Long.parseLong(((Item) target.getSelectedItem()).getId()));
	        //String networkName = network.getRow(network).get(CyNetwork.NAME, String.class);
        	
        	CyTable nodeTbl = network.getDefaultNodeTable();
            CyTable edgeTbl = network.getDefaultEdgeTable();
            
            //String[] nodeAttrNames = CyTableUtil.getColumnNames(nodeTbl).toArray(new String[0]);
            //String[] edgeAttrNames = CyTableUtil.getColumnNames(edgeTbl).toArray(new String[0]);
            ArrayList<String> nodeAttrNames = new ArrayList<String>();
            ArrayList<String> edgeAttrNames = new ArrayList<String>();
            
            //ciclo sugli attributi dei nodi
            ArrayList<CyColumn> nodeColumns = (ArrayList<CyColumn>) nodeTbl.getColumns();
            Iterator<CyColumn> nodeColumnsIterator = nodeColumns.iterator();
			while(nodeColumnsIterator.hasNext()) {
				CyColumn nodeColumn = nodeColumnsIterator.next();
				
				String nodeColumnName = nodeColumn.getName();
				Class<?> type = nodeColumn.getType();
				
				//if (type.getName().equals(String.class.getName()))
				if (!nodeColumnName.equals(Strings.SUID) && !nodeColumnName.equals(Strings.SELECTED))
					if (!Collection.class.isAssignableFrom(type))
						nodeAttrNames.add(nodeColumnName);
			}
			
			//ciclo sugli attributi degli archi
			ArrayList<CyColumn> edgeColumns = (ArrayList<CyColumn>) edgeTbl.getColumns();
			Iterator<CyColumn> edgeColumnsIterator = edgeColumns.iterator();
			while(edgeColumnsIterator.hasNext()) {
				CyColumn edgeColumn = edgeColumnsIterator.next();
				
				String edgeColumnName = edgeColumn.getName();
				Class<?> type = edgeColumn.getType();
				
				//if (type.getName().equals(String.class.getName()))
				if (!edgeColumnName.equals(Strings.SUID) && !edgeColumnName.equals(Strings.SELECTED))
					if (!Collection.class.isAssignableFrom(type))
	        			edgeAttrNames.add(edgeColumnName);
			}
			
			Iterator<String> nodeAttrNamesIterator = nodeAttrNames.iterator();
			while (nodeAttrNamesIterator.hasNext()) {
				String nodeAttrName = nodeAttrNamesIterator.next();
				tna.addItem(nodeAttrName);
			}
			tna.addItem(Strings.LIST_ATTRIBUTES);
			
			Iterator<String> edgeAttrNamesIterator = edgeAttrNames.iterator();
			while (edgeAttrNamesIterator.hasNext()) {
				String edgeAttrName = edgeAttrNamesIterator.next();
				tea.addItem(edgeAttrName);
			}
			tea.addItem(Strings.LIST_ATTRIBUTES);
			
          	if(oldTea != null) {
          		for(int i = 0;i < tea.getItemCount();i++) {
          			Object o1 = tea.getItemAt(i);
          			if(o1.equals(oldTea)) {
          				tea.setSelectedItem(oldTea);
          				break;
          			}
          		}
          	}
          	if(oldTna != null) {
          		for(int i = 0;i < tna.getItemCount();i++) {
          			Object o1 = tna.getItemAt(i);
          			if(o1.equals(oldTna)) {
          				tna.setSelectedItem(oldTna);
          				break;
          			}   
          		}
          	}
          	
          	target.addActionListener(this);
		    tea.addActionListener(this);
		    tna.addActionListener(this);
		}
	} 
	
	private void loadGFFFile(File file) {
		System.out.println("Loading GFF file " + file.getName());
		
		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
    	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
    	TaskIterator taskIterator = new TaskIterator();
	  
    	LoadGFFFileTask loadGffFileTask = new LoadGFFFileTask(adapter, file);
    	taskIterator.append(loadGffFileTask);
    	dialogTaskManager.execute(taskIterator);
	}

	private void saveQueryNetwork() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFileChooser fc = new JFileChooser();
				SIFFilter filter = new SIFFilter();
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				
				CyApplicationManager am = adapter.getCyApplicationManager();
	      		CyNetwork currentNetwork = am.getCurrentNetwork();
	      		String currentNetworkName = currentNetwork.getRow(currentNetwork).get(CyNetwork.NAME, String.class);
	      		
				if (!currentNetworkName.startsWith("QueryNetwork")) {
	      			JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(),
	      		    		"You can save only a Query Network!", "NetMatch*", JOptionPane.WARNING_MESSAGE);
	      		}
				else {
					int result = fc.showSaveDialog(new Component() {});
					if (result == JFileChooser.APPROVE_OPTION) {
		      		
		      			HashMap<String, String> nodesMap = new HashMap<String, String>();
		      			CyTable nodeTable = currentNetwork.getDefaultNodeTable();
		      			for (CyNode node : currentNetwork.getNodeList()) {
		      				String sharedNameValue = currentNetwork.getRow(node).get(Common.NODE_ID_ATTR, String.class);
		      				String labelValue = currentNetwork.getRow(node).get(Common.NODE_QUERY_ATTR, String.class);
		      				nodesMap.put(sharedNameValue, labelValue);
		      			}
		      			HashMap<String, String> edgesMap = new HashMap<String, String>();
		      			CyTable edgeTable = currentNetwork.getDefaultEdgeTable();
		      			for (CyEdge edge : currentNetwork.getEdgeList()) {
		      				String sharedNameValue = currentNetwork.getRow(edge).get(Common.EDGE_ID_ATTR, String.class);
		      				String labelValue = currentNetwork.getRow(edge).get(Common.EDGE_QUERY_ATTR, String.class);
		      				edgesMap.put(sharedNameValue, labelValue);
		      			}
		      			
		      			String fileName = fc.getSelectedFile().getName();
		      			
		      			//if (!fileName.endsWith(".sif"))
		      				//fileName += ".sif";
		      			
		          		File file = fc.getSelectedFile();
		          		if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("sif")) {
		          			//file = new File(file.toString() + ".sif");
		          		    file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())+".sif");
		          		}
		          		int opt = 0;
		          		if(file.exists()) {
		          			opt = JOptionPane.showConfirmDialog(null,"The file alredy exists. Overwrite?", "NetMatch*",
		          					JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
		          		}
		          		if(opt == 0) {
		          			//FileFilter selectedFilter = (FileFilter)fc.getFileFilter();
		          			
		          			CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
				        	PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
				        	TaskIterator taskIterator = new TaskIterator();
						  
				        	SaveQueryNetworkTask saveTask = new SaveQueryNetworkTask(nodesMap, edgesMap, file);
				        	taskIterator.append(saveTask);
				        	dialogTaskManager.execute(taskIterator);
		          		}
		          		
		          		CyRow currentNetworkRow = currentNetwork.getRow(currentNetwork);
		          		currentNetworkRow.set(CyNetwork.NAME, "QueryNetwork-"+fileName);
		          		acquireData("");
					}
		      	}
			}	
		}); 
    }

	private boolean areThereAttributeFiles(File nodeAttrFile, File edgeAttrFile) {
		return nodeAttrFile.exists() && edgeAttrFile.exists();
	}

	private HashMap<String, String> loadNodeAttributes(String nodeAttrFileName) throws InvalidSIFException {
		HashMap<String, String> nodeAttributes = new HashMap<String, String>();
		try {
			File nodeAttrFile = new File(nodeAttrFileName);
			BufferedReader reader = new BufferedReader(new FileReader(nodeAttrFile));
			String line = reader.readLine(); //Legge la prima riga
			int lineNumber = 1; //Gli attributi cominciano dalla seconda riga
			while((line = reader.readLine()) != null) {
				lineNumber++;
				String l = line.trim();
				String ss[] = l.split(" = ");
				if (ss.length == 2) {
					String n = ss[0].trim();
					String a = ss[1].trim();
					nodeAttributes.put(n, a);
				}
				else
					throw new InvalidSIFException(lineNumber);
	      	}
		} catch (IOException ex) {
			System.err.println(ex.getCause());
	    }
		
		return nodeAttributes;
	}
	
	private HashMap<String, String> loadEdgeAttributes(String edgeAttrFileName) throws InvalidSIFException {
		HashMap<String, String> edgeAttributes = new HashMap<String, String>();
	    try {
	    	File edgeAttrFile = new File(edgeAttrFileName);
			BufferedReader reader = new BufferedReader(new FileReader(edgeAttrFile));
	        String line = reader.readLine(); //Legge la prima riga
	        int lineNumber = 1; //Gli attributi cominciano dalla seconda riga
	        while((line = reader.readLine()) != null) {
		        lineNumber++;
		        String l = line.trim();
				String ss[] = l.split(" = ");
				if (ss.length == 2) {
					String e = ss[0].trim();
					String a = ss[1].trim();
					edgeAttributes.put(e, a);
				}
				else
					throw new InvalidSIFException(lineNumber);
	        }
	    } catch (IOException ex) {
	    	System.err.println(ex.getMessage());
	    }
	    
	    return edgeAttributes;
	}

	/**
	 * Check if the query network contains paths (is approximate)
	 * @param qnetwork
	 * @return 
	 */
	private boolean isQueryApproximate(CyNetwork qNetwork, String queryEdgeAttribute) {
		for (CyEdge edge : qNetwork.getEdgeList()) {
			CyRow edgeRow = qNetwork.getRow(edge);
			Class<?> type = edgeRow.getTable().getColumn(queryEdgeAttribute).getType();
			
			String valueAttr = null;
			if (!Collection.class.isAssignableFrom(type)) {
				valueAttr = edgeRow.get(queryEdgeAttribute, type).toString();
			}
			
			if (valueAttr != null) { //l'attributo non è una lista
				if (/*valueAttr.equals("?") || */Common.isApproximatePath(valueAttr)) //l'arco è approssimato
					return true;
			}
		}
		
		return false;
	}
	
	private boolean isQueryUnlabeled(CyNetwork qNetwork, String queryNodeAttribute, String queryEdgeAttribute) {
		for (CyNode node : qNetwork.getNodeList()) {
			CyRow nodeRow = qNetwork.getRow(node);
			Class<?> type = nodeRow.getTable().getColumn(queryNodeAttribute).getType();
			
			String valueAttr = null;
			if (!Collection.class.isAssignableFrom(type)) {
				valueAttr = nodeRow.get(queryNodeAttribute, type).toString();
			}
			
			if (valueAttr != null) {
				if (valueAttr.equals("?")) {
					return true;
				}
			}
		}
		
		for (CyEdge edge: qNetwork.getEdgeList()) {
			CyRow edgeRow = qNetwork.getRow(edge);
			Class<?> type = edgeRow.getTable().getColumn(queryEdgeAttribute).getType();
			
			String valueAttr = null;
			if (!Collection.class.isAssignableFrom(type)) {
				valueAttr = edgeRow.get(queryEdgeAttribute, type).toString();
			}
			
			if (valueAttr != null) {
				if (valueAttr.equals("?")) {
					return true;
				}
			}
		}
		
		return false;
	}

	public void close() throws Exception {
        if(help != null) {
        	help.setVisible(false);
        	help.dispose();
        }
        if(aboutbox != null) {
        	aboutbox.setVisible(false);
        	aboutbox.dispose();
        }
        if(matchingPanel != null) {
			matchingPanel.setVisible(false);
        	MenuAction.setOpened(false);
        	WestPanel panel = this;
        	CyAppAdapter adapt = MenuAction.getAdapter();
        	CyServiceRegistrar csr=adapt.getCyServiceRegistrar();
        	csr.unregisterAllServices(panel);
        	//pan.dispose();
        }
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setAttrList(ArrayList attrs, boolean isNodeAttr) {
	    JComboBox c = isNodeAttr ? tna : tea;
	    c.removeActionListener(this);
	    if(attrs == null) {
	    	c.removeItem(Strings.LIST_ATTRIBUTES);
	    	c.removeItem(Strings.LIST_ATTRIBUTES_CHANGED);
	    	c.addItem(Strings.LIST_ATTRIBUTES);
	    	c.setSelectedIndex(0);
	    	if(isNodeAttr)
	    		listOfNodeAttributes = null;
	    	else
	    		listOfEdgeAttributes = null;
	    }
	    else {
	    	c.removeItem(Strings.LIST_ATTRIBUTES);
	    	c.removeItem(Strings.LIST_ATTRIBUTES_CHANGED);
	    	c.addItem(Strings.LIST_ATTRIBUTES_CHANGED);
	    	c.setSelectedItem(Strings.LIST_ATTRIBUTES_CHANGED);
	    	if(isNodeAttr)
	    		listOfNodeAttributes = attrs;
	    	else
	    		listOfEdgeAttributes = attrs;
		}
	    c.addActionListener(this);
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void acquireData(String networkDestroyed) {
	    query.removeActionListener(this);
	    target.removeActionListener(this);
	    tea.removeActionListener(this);
	    tna.removeActionListener(this);

	    Object oldQuery = query.getSelectedItem();
	    Object oldTarget = target.getSelectedItem();
	    Object oldQea = qea.getSelectedItem();
	    Object oldQna = qna.getSelectedItem();
	    Object oldTea = tea.getSelectedItem();
	    Object oldTna = tna.getSelectedItem();

	    query.removeAllItems();
	    target.removeAllItems();
	    qea.removeAllItems();
	    qna.removeAllItems();
	    tea.removeAllItems();
	    tna.removeAllItems();

	    query.setEnabled(false);
	    target.setEnabled(false);
	    tea.setEnabled(false);
	    tna.setEnabled(false);
	    qea.setEnabled(false);
	    qna.setEnabled(false);
	    
	    for(Object value : s) {
	    	CyNetwork n = (CyNetwork) value;
	    	if(!n.getSUID().equals(networkDestroyed)) {
	    		//query.addItem(n.getSUID() + "-" + n.getRow(n).get(CyNetwork.NAME, String.class));
	    		/*query.addItem(n.getSUID().toString()+"-"+ n.getRow(n).get(CyNetwork.NAME, String.class));
	    	  	query.setEnabled(true);
	  	    
    		  	//target.addItem(n.getSUID() + "-" + n.getRow(n).get(CyNetwork.NAME, String.class));
	    	  	target.addItem(n.getSUID().toString()+"-"+ n.getRow(n).get(CyNetwork.NAME, String.class));
	    	  	target.setEnabled(false);*/
	    	  
	    		String id = n.getSUID().toString();
	    		String description = n.getRow(n).get(CyNetwork.NAME, String.class);
	    	  
	    		query.addItem(new Item(id, description));
	    		if (!description.startsWith("QueryNetwork"))
	    			target.addItem(new Item(id, description));
	    	  
	    		queryNetworkIdMap.put(description, id);
	    		targetNetworkIdMap.put(description, id);
	    	}
	    }
	    
	    //Query network
	    /*for(Object o1 : queries.keySet()) {
	      	Object o = "QE-" + o1;
	      	query.addItem(o);
	      	qea.addItem(o + " - Edges Attributes");
	      	qna.addItem(o + " - Nodes Attributes");
	    }*/
	    Item item = (Item) query.getSelectedItem();
	    
	    CyNetwork queryNetwork = cnm.getNetwork(Long.parseLong(((Item) query.getSelectedItem()).getId()));
	    String queryNetworkName = queryNetwork.getRow(queryNetwork).get(CyNetwork.NAME, String.class);
	    
	    CyTable qNodeTbl = queryNetwork.getDefaultNodeTable();
        CyTable qEdgeTbl = queryNetwork.getDefaultEdgeTable();
        
        ArrayList<String> qNodeAttrNames = new ArrayList<String>();
        ArrayList<String> qEdgeAttrNames = new ArrayList<String>();
        
	    if(query.getItemCount() > 0) {
	    	query.setEnabled(true);
	    	if(oldQuery != null) {
	    		for(int i = 0;i < query.getItemCount();i++) {
	    			Object o = query.getItemAt(i);
	    			if(o.equals(oldQuery)) {
	    				query.setSelectedItem(oldQuery);
	    				break;
	    			}
	    		}
	    	}
	    
	    	/*int indexNet = query.getSelectedIndex();
	      	int currentNetworkSUID = (Integer) list.get(indexNet);
	      	CyNetwork currentNetwork = cnm.getNetwork(currentNetworkSUID);
	      	CyTable nodeAttributes = currentNetwork.getDefaultNodeTable();
	      	CyTable edgeAttributes = currentNetwork.getDefaultEdgeTable();
	    	 */
	      
	    	//ciclo sugli attributi dei nodi della query
	    	ArrayList<CyColumn> qNodeColumns = (ArrayList<CyColumn>) qNodeTbl.getColumns();
	    	Iterator<CyColumn> qNodeColumnsIterator = qNodeColumns.iterator();
	    	while(qNodeColumnsIterator.hasNext()) {
	    		CyColumn qNodeColumn = qNodeColumnsIterator.next();
				
	    		String qNodeColumnName = qNodeColumn.getName();
	    		Class<?> type = qNodeColumn.getType();
	        	  
	    		if (queryNetworkName.startsWith("QueryNetwork")) {
	    			if (qNodeColumnName.equals(Common.NODE_QUERY_ATTR))
	    				qNodeAttrNames.add(qNodeColumnName);
	    		}
	    		else { 
	    			if (!qNodeColumnName.equals(Strings.SUID) && !qNodeColumnName.equals(Strings.SELECTED)) {
	    				if (!Collection.class.isAssignableFrom(type))
	    					qNodeAttrNames.add(qNodeColumnName);
	    			}
	    		}
	    	}
			
	    	//ciclo sugli attributi degli archi della query
	    	ArrayList<CyColumn> qEdgeColumns = (ArrayList<CyColumn>) qEdgeTbl.getColumns();
	    	Iterator<CyColumn> qEdgeColumnsIterator = qEdgeColumns.iterator();
	    	while(qEdgeColumnsIterator.hasNext()) {
	    		CyColumn qEdgeColumn = qEdgeColumnsIterator.next();
        	  
	    		String qEdgeColumnName = qEdgeColumn.getName();
	    		Class<?> type = qEdgeColumn.getType();
        	  
	    		if (queryNetworkName.startsWith("QueryNetwork")) {
	    			if (qEdgeColumnName.equals(Common.EDGE_QUERY_ATTR))
	    				qEdgeAttrNames.add(qEdgeColumnName);
	    		}
	    		else { 
	    			if (!qEdgeColumnName.equals(Strings.SUID) && !qEdgeColumnName.equals(Strings.SELECTED)) {
	    				if (!Collection.class.isAssignableFrom(type))
	    					qEdgeAttrNames.add(qEdgeColumnName);
	    			}
	    		}
	    	}
          
	    	Iterator<String> qNodeAttrNamesIterator = qNodeAttrNames.iterator();
	    	while (qNodeAttrNamesIterator.hasNext()) {
	    		String qNodeAttrName = qNodeAttrNamesIterator.next();
	    		qna.addItem(qNodeAttrName);
	    	}
			
	    	Iterator<String> qEdgeAttrNamesIterator = qEdgeAttrNames.iterator();
	    	while (qEdgeAttrNamesIterator.hasNext()) {
	    		String qEdgeAttrName = qEdgeAttrNamesIterator.next();
	    		qea.addItem(qEdgeAttrName);
	    	}
	      
	    	/*for(int i = 0;i < edgeAttrNames.length;i++)
	    	  	qea.addItem(edgeAttrNames[i]);
	      	for(int i = 0;i < nodeAttrNames.length;i++)
	    	  	qna.addItem(nodeAttrNames[i]);*/
	    	
	    	if (oldQea != null) {
	    		for(int i = 0;i < qea.getItemCount();i++) {
	    			Object o = qea.getItemAt(i);
	    			if(o.equals(oldQea)) {
	    				qea.setSelectedItem(oldQea);
	    				break;
	    			}
	    		}
	    	}
	    	if (oldQna != null) {
	        for(int i = 0;i < qna.getItemCount();i++) {
	        	Object o = qna.getItemAt(i);
	        	if(o.equals(oldQna)) {
	        		qna.setSelectedItem(oldQna);
	        		break;
	        	}
	        	}
	    	}
	    	qna.setEnabled(true);
	    	qea.setEnabled(true);
	    	}

		    listOfNodeAttributes = new ArrayList();
		    listOfEdgeAttributes = new ArrayList();
		    
		    CyNetwork targetNetwork = cnm.getNetwork(Long.parseLong(((Item) target.getSelectedItem()).getId()));
		    
		    CyTable tNodeTbl = targetNetwork.getDefaultNodeTable();
	        CyTable tEdgeTbl = targetNetwork.getDefaultEdgeTable();
	        
	        ArrayList<String> tNodeAttrNames = new ArrayList<String>();
	        ArrayList<String> tEdgeAttrNames = new ArrayList<String>();
	        
	        if(target.getItemCount() > 0) {
	        	target.setEnabled(true);
	        	if(oldTarget != null) {
	        		for(int i = 0;i < target.getItemCount();i++) {
	        			Object o = target.getItemAt(i);
	        			if(o.equals(oldTarget)) {
	        				target.setSelectedItem(oldTarget);
	        				break;
	        			}
	        		}
	        	}
	      
	        	//ciclo sugli attributi dei nodi del target
	        	ArrayList<CyColumn> tNodeColumns = (ArrayList<CyColumn>) tNodeTbl.getColumns();
	        	Iterator<CyColumn> tNodeColumnsIterator = tNodeColumns.iterator();
	        	while(tNodeColumnsIterator.hasNext()) {
	        		CyColumn tNodeColumn = tNodeColumnsIterator.next();
				
	        		String tNodeColumnName = tNodeColumn.getName();
	        		Class<?> type = tNodeColumn.getType();
				
	        		if (!tNodeColumnName.equals(Strings.SUID) && !tNodeColumnName.equals(Strings.SELECTED))
	        			if (!Collection.class.isAssignableFrom(type))
	        				tNodeAttrNames.add(tNodeColumnName);
	        	}
			
	        	//ciclo sugli attributi degli archi del target
	        	ArrayList<CyColumn> tEdgeColumns = (ArrayList<CyColumn>) tEdgeTbl.getColumns();
	        	Iterator<CyColumn> tEdgeColumnsIterator = tEdgeColumns.iterator();
	        	while(tEdgeColumnsIterator.hasNext()) {
	        		CyColumn tEdgeColumn = tEdgeColumnsIterator.next();
        	  
	        		String tEdgeColumnName = tEdgeColumn.getName();
	        		Class<?> type = tEdgeColumn.getType();
        	  
	        		if (!tEdgeColumnName.equals(Strings.SUID) && !tEdgeColumnName.equals(Strings.SELECTED))
	        			if (!Collection.class.isAssignableFrom(type))
	        				tEdgeAttrNames.add(tEdgeColumnName);
	        	}
          
	          Iterator<String> tNodeAttrNamesIterator = tNodeAttrNames.iterator();
	          while (tNodeAttrNamesIterator.hasNext()) {
	        	  String tNodeAttrName = tNodeAttrNamesIterator.next();
	        	  tna.addItem(tNodeAttrName);
	          }
	          tna.addItem(Strings.LIST_ATTRIBUTES);
			
	          //ciclo sugli attributi degli archi del target
	          Iterator<String> tEdgeAttrNamesIterator = tEdgeAttrNames.iterator();
	          while (tEdgeAttrNamesIterator.hasNext()) {
	        	  String edgeAttrName = tEdgeAttrNamesIterator.next();
	        	  tea.addItem(edgeAttrName);
	          }
	          tea.addItem(Strings.LIST_ATTRIBUTES);
	          
		      /*for(int i = 0;i < edgeAttrNames.length;i++)
		        tea.addItem(edgeAttrNames[i]);
		      tea.addItem(Strings.LIST_ATTRIBUTES);
		      for(int i = 0;i < nodeAttrNames.length;i++)
		        tna.addItem(nodeAttrNames[i]);
		      tna.addItem(Strings.LIST_ATTRIBUTES);*/

	          if(oldTea != null) {
	        	  for(int i = 0;i < tea.getItemCount();i++) {
	        		  Object o = tea.getItemAt(i);
	        		  if(o.equals(oldTea)) {
	        			  tea.setSelectedItem(oldTea);
	        			  break;
	        		  }
	        	  }
	          }
	          if(oldTna != null) {
	        	  for(int i = 0;i < tna.getItemCount();i++) {
	        		  Object o = tna.getItemAt(i);
	        		  if(o.equals(oldTna)) {
	        			  tna.setSelectedItem(oldTna);
	        			  break;
	        		  }
	        	  }
	          }
		      tea.setEnabled(true);
		      tna.setEnabled(true);
		      listOfNodeAttributes.add(tna.getSelectedItem());
		      listOfEdgeAttributes.add(tea.getSelectedItem());
	    }
	    query.addActionListener(this);
	    target.addActionListener(this);
	    tna.addActionListener(this);
	    tea.addActionListener(this);
	}//acquireData

	@SuppressWarnings("unchecked")
	public class MyNetworkAddedListener implements NetworkAddedListener, NetworkAboutToBeDestroyedListener {
		private CyAppAdapter adapter;
		
		public MyNetworkAddedListener(CyAppAdapter adapter2) {
			this.setAdapter(adapter2);
		}
		
		public void handleEvent(NetworkAddedEvent e) {
			CyNetwork network = e.getNetwork();
			s.add(network);
			
			CyRow networkRow = network.getRow(network);
			CyTable networkTable = network.getDefaultNetworkTable();
			ArrayList<CyColumn> columns = (ArrayList<CyColumn>) networkTable.getColumns();
			CyColumn attrCol = columns.get(1);
			String attrName = attrCol.getName();
			
			Class<?> type = attrCol.getType();
			String networkName = (String)networkRow.get(attrName, type);
			
			String fileName = currentPath + "/" +networkName;
			//String fileName = networkName;
			String baseName = FilenameUtils.getBaseName(fileName);
			String nodeAttrFileName = fileName.substring(0,fileName.length()-3)+"NA";
	        File nodeAttrFile = new File(nodeAttrFileName);
	        String edgeAttrFileName = fileName.substring(0,fileName.length()-3)+"EA";
	        File edgeAttrFile = new File(edgeAttrFileName);
	        
	        HashMap<String, String> nodeAttributes = null;
	        HashMap<String, String> edgeAttributes = null;
	        if(areThereAttributeFiles(nodeAttrFile, edgeAttrFile)) { 
	        	//loadNetworkAttributes(nodeAttrFileName, edgeAttrFileName);
	        	try {
					nodeAttributes = loadNodeAttributes(nodeAttrFileName);
					edgeAttributes = loadEdgeAttributes(edgeAttrFileName);
	        	} catch (InvalidSIFException e1) {
					// TODO Blocco catch generato automaticamente
					e1.printStackTrace();
				}
	        	networkRow.set(CyNetwork.NAME, "QueryNetwork-"+networkName);
				networkRow.set(attrName, "QueryNetwork-"+networkName);
				networkRow.set("name", "QueryNetwork-"+networkName); 
				
				CyTable nodeTable = network.getDefaultNodeTable();
				//String columnName = Common.NODE_QUERY_ATTR;
				nodeTable.createColumn(Common.NODE_QUERY_ATTR, String.class, true); //immutable
				
				CyTable edgeTable = network.getDefaultEdgeTable();
				edgeTable.createColumn(Common.EDGE_QUERY_ATTR, String.class, true); //immutable
				
				List<CyNode> nodesList = network.getNodeList();
				List<CyEdge> edgesList = network.getEdgeList();
				
				if (nodeAttributes != null) {
					Iterator<CyNode> nodesIterator = nodesList.iterator();
					while(nodesIterator.hasNext()) {
						CyNode node = nodesIterator.next();
						Long suid = node.getSUID();
						String nodeId = nodeTable.getRow(suid).get(Common.NODE_ID_ATTR, String.class);
						String attrValue = nodeAttributes.get(nodeId);
						nodeTable.getRow(suid).set(Common.NODE_QUERY_ATTR, attrValue);
					}
				}
				
				if (edgeAttributes != null) {
					Iterator<CyEdge> edgesIterator = edgesList.iterator();
					while(edgesIterator.hasNext()) {
						CyEdge edge = edgesIterator.next();
						Long suid = edge.getSUID();
						String edgeId = edgeTable.getRow(suid).get(Common.EDGE_ID_ATTR, String.class);
						String attrValue = edgeAttributes.get(edgeId);
						edgeTable.getRow(suid).set(Common.EDGE_QUERY_ATTR, attrValue);
					}
				}
	        }
	        else
	        	networkRow.set(CyNetwork.NAME, baseName); //TODO Controllare
	        
	        acquireData("");
		}
		
		public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
			CyNetwork network = e.getNetwork();
			s.remove(network);
			acquireData("");
		}
		
		public CyAppAdapter getAdapter() {
			return adapter;
		}
		public void setAdapter(CyAppAdapter adapter) {
			this.adapter = adapter;
		}
	}//class MyNetworkAddedListener
	
	/**
	 * 
	 * @author Fabio Rinnone
	 */
	public class MyNetworkViewAddedListener implements NetworkViewAddedListener {
		private CySwingAppAdapter adapter;
		private CyNetworkView netView;
		private CyNetwork net;
		private VisualStyle vs;
		private VisualMappingManager manager;
		private Long suid;
		private Integer motifType;
		
		public MyNetworkViewAddedListener(CySwingAppAdapter adapt) {
			this.adapter = adapt;
		}

		@Override
		public void handleEvent(NetworkViewAddedEvent e) {
			netView = e.getNetworkView();
			net = netView.getModel();
			String netName = net.getRow(net).get(CyNetwork.NAME, String.class);
			vs = null;
			manager = adapter.getVisualMappingManager();
			suid = net.getSUID();
			
			CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			PanelTaskManager dialogTaskManager = csr.getService(PanelTaskManager.class);
	    	
			if (netName.startsWith("QueryNetwork")) {
				VisualStyleFactory vsf = adapter.getVisualStyleFactory();
				Set<VisualStyle> visualStyles = manager.getAllVisualStyles();
				
				boolean vsFound = false;
				Iterator<VisualStyle> iterator = visualStyles.iterator();
				VisualStyle currentVs = null;
				while(iterator.hasNext() && !vsFound) {
					currentVs = iterator.next();
					if (currentVs.getTitle().equals(Common.NETMATCH_STYLE))
						vsFound = true;
				}
				
				if (vsFound)
					vs = currentVs;
				else
					vs = vsf.createVisualStyle(Common.NETMATCH_STYLE);
				//NetworkUtils.configureQueryVisualStyle(vs, adapter);
				
				if (!NetworkUtils.isAMotif(suid)) {
					TaskIterator taskIterator = new TaskIterator();	  
			    	QueryLayoutTask task = new QueryLayoutTask(adapter, vs, netView);
			    	taskIterator.append(task);
			    	dialogTaskManager.execute(taskIterator);
				}
				
				if (!visualStyles.contains(vs))
					manager.addVisualStyle(vs);			
			}
			else
				vs = manager.getDefaultVisualStyle();
			
			manager.setVisualStyle(vs, netView);
			vs.apply(netView);
			
			if (netName.startsWith("QueryNetwork")) {
				if (NetworkUtils.isAMotif(suid)) {
					motifType = Common.motifsMap.get(suid);
					TaskIterator taskIterator = new TaskIterator();
			    	MotifLayoutTask task = new MotifLayoutTask(adapter, vs, netView, motifType);
			    	taskIterator.append(task);
			    	dialogTaskManager.execute(taskIterator);
				}
			}
			else {
				TaskIterator taskIterator = new TaskIterator();
		    	NetworkLayoutTask task = new NetworkLayoutTask(adapter, vs, netView);
		    	taskIterator.append(task);
		    	dialogTaskManager.execute(taskIterator);
			}
		}
	}//class MyNetworkViewAddedListener
	
	private class MyAddedNodesListener implements AddedNodesListener {
		CyAppAdapter adapter;
		
		public MyAddedNodesListener(CyAppAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public void handleEvent(AddedNodesEvent e) {
			CyNetwork cyNetwork = e.getSource();
			List<CyNode> nodesList = cyNetwork.getNodeList();
			CyNode lastNodeAdded = nodesList.get(0); //l'ultimo nodo è sempre aggiunto in testa
			cyNetwork.getDefaultNodeTable().getRow(lastNodeAdded.getSUID()).set(Common.NODE_QUERY_ATTR, "?");
		}
		
	}//class MyAddedNodesListener
	
	private class MyAddedEdgesListener implements AddedEdgesListener {
		CyAppAdapter adapter;
		
		public MyAddedEdgesListener(CyAppAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public void handleEvent(AddedEdgesEvent e) {
			CyNetwork cyNetwork = e.getSource();
			List<CyEdge> edgesList = cyNetwork.getEdgeList();
			CyEdge lastEdgeAdded = edgesList.get(0); //l'ultimo arco è sempre aggiunto in testa
			cyNetwork.getDefaultEdgeTable().getRow(lastEdgeAdded.getSUID()).set(Common.EDGE_QUERY_ATTR, "?");
		}
		
	}//class MyAddedEdgesListener
	
	private class MyColumnCreatedListener implements ColumnCreatedListener {
		CyAppAdapter adapter;
		
		public MyColumnCreatedListener(CyAppAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public void handleEvent(ColumnCreatedEvent e) {
			//acquireData("");
		}
	}
	
	private class MyColumnDeletedListener implements ColumnDeletedListener {
		CyAppAdapter adapter;
		
		public MyColumnDeletedListener(CyAppAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public void handleEvent(ColumnDeletedEvent e) {
			//acquireData("");
		}
	}
	
	/**
	 * 
	 * @author Fabio Rinnone
	 */
	private class ButtonListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	JButton bt = (JButton) e.getSource();
    	
	    	for(int i = 0; i < sButtons.length; i++) {
	    		if(sButtons[i] == bt) {
		        	if (i == 0) //Three Chain
		        		loadThreeChain();
		        	else if (i == 1) //Feed Forward Loop
		        		loadFeedForwardLoop();
		        	else if (i == 2) //Bi-Parallel
		        		loadBiParallel();
		        	else if (i == 3) //Bi-Fan
		        		loadBiFan();
		        	else //mton-Fan
		        		loadMtonFan();
	        	}
	    	}
	    }
	}//class ButtonListener
	
	private void loadThreeChain() {
		CyNetwork threeChain = NetworkUtils.createThreeChainNetwork(adapter);			
		cnm.addNetwork(threeChain);
		Common.indexN++;
		NetworkUtils.addNetworkView(adapter, threeChain);
	}
	
	private void loadFeedForwardLoop() {
		CyNetwork feedForwardLoop = NetworkUtils.createFeedForwardLoopNetwork(adapter);
		cnm.addNetwork(feedForwardLoop);
		Common.indexN++;
		NetworkUtils.addNetworkView(adapter, feedForwardLoop);
	}
	
	private void loadBiParallel() {
		CyNetwork biParallel = NetworkUtils.createBiParallelNetwork(adapter);
		cnm.addNetwork(biParallel);
		Common.indexN++;
		NetworkUtils.addNetworkView(adapter, biParallel);
	}
	
	private void loadBiFan() {
		CyNetwork biFan = NetworkUtils.createBiFanNetwork(adapter);
		cnm.addNetwork(biFan);
		Common.indexN++;
		NetworkUtils.addNetworkView(adapter, biFan);
	}
	
	private void loadMtonFan() {
		CyNetwork mtonFan = NetworkUtils.createMtonFanNetwork(adapter);
		if (mtonFan != null) {
	        cnm.addNetwork(mtonFan);
	        Common.indexN++;
	        NetworkUtils.addNetworkView(adapter, mtonFan);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Stub di metodo generato automaticamente
		
	}
}//class WestPanel