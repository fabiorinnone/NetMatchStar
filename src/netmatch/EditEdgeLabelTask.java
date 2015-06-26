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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractEdgeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class EditEdgeLabelTask extends AbstractEdgeViewTask {
	private CySwingAppAdapter adapter;
	
	private Object[] message;
	private JTextField textField;
	private JRadioButton inexButton;
	private JRadioButton exButton;
	private ButtonGroup group;
	private JPanel p;
	private String[] options;
	private int result;
	private String oldAttr;
	private String attribute = "";

	private CyRow edgeRow;
	
	public EditEdgeLabelTask(View<CyEdge> edgeView, CyNetworkView netView, CySwingAppAdapter adapter) {
		super(edgeView, netView);
		this.adapter = adapter; 
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		CyNetwork network = netView.getModel();
		CyEdge edge = edgeView.getModel();
		edgeRow = network.getRow(edge);
	
		oldAttr = edgeRow.get(Common.EDGE_QUERY_ATTR, String.class);
		
		message = new Object[3];
		
		textField = new JTextField(25);
		
		inexButton = new JRadioButton("Unlabeled.");
	    inexButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	    inexButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JRadioButton o = (JRadioButton) e.getSource();
	    			if(o.isSelected())
	    				textField.setEnabled(false);
	    	}
	    });
	    
	    exButton = new JRadioButton("Labeled: ");
	    exButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	    exButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JRadioButton o = (JRadioButton) e.getSource();
	    		if(o.isSelected())
	    			textField.setEnabled(true);
	    	}
	    });
	    
	    if(oldAttr.charAt(0) == '?') {
	    	inexButton.setSelected(true);
	        textField.setEnabled(false);
	    }
	    else {
	    	exButton.setSelected(true);
	        textField.setEnabled(true);
	        textField.setText(oldAttr);
	    }
	    
	    group = new ButtonGroup();
	    group.add(inexButton);
	    group.add(exButton);
	    
		p = new JPanel(new BorderLayout(5, 5));
		p.add(exButton, BorderLayout.WEST);
	    p.add(textField, BorderLayout.CENTER);
	    
	    message[0] = "Please enter attribute:";
	    message[1] = inexButton;
	    message[2] = p;
	    
	    options = new String[2];
	    options[0] = "OK";
	    options[1] = "Cancel";
	    
		SwingUtilities.invokeLater(new Runnable() {
      		public void run() {
  				JFrame frame = adapter.getCySwingApplication().getJFrame();
      			int result = JOptionPane.showOptionDialog (
  						frame, //the parent that the dialog blocks
      			        message, //the dialog message array
      			        "Edit Attribute", //the title of the dialog window
      			        JOptionPane.DEFAULT_OPTION, //option type
      			        JOptionPane.INFORMATION_MESSAGE, //message type
      			        null, //optional icon, use null to use the default icon
      			        options, //options string array, will be made into buttons
      			        options[0] //option that should be made into a default button
				);
      			
      			if (result == 0) {
      				if(inexButton.isSelected())
      					attribute = "?";
      				else 
      					if (textField.getText().equals(""))
      						attribute = "?";
      					else 
      						attribute = textField.getText();
      				edgeRow.set(Common.EDGE_QUERY_ATTR, attribute);
      			}
      			else 
      				edgeRow.set(Common.EDGE_QUERY_ATTR, oldAttr);
      		}
		});
	}
	
}