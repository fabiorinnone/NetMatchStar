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

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import netmatch.utils.Common;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.task.AbstractEdgeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class SetApproxPathTask extends AbstractEdgeViewTask {
	private CySwingAppAdapter adapter;
	
	private Object[] message;
	private String[] constraints;
	private JComboBox list;
	private JSpinner spinner;
	private JPanel p;
	private String[] options;
	private String oldAttr;
	private String attribute = "";

	private CyRow edgeRow;
	
	
	public SetApproxPathTask(View<CyEdge> edgeView, CyNetworkView netView, CySwingAppAdapter adapter) {
		super(edgeView, netView);
		this.adapter = adapter;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		CyNetwork network = netView.getModel();
		CyEdge edge = edgeView.getModel();
		edgeRow = network.getRow(edge);
		
		oldAttr = edgeRow.get(Common.EDGE_QUERY_ATTR, String.class);
			
		message = new Object[2];
		
		constraints = new String[5];
		constraints[0] = Common.GT;
		constraints[1] = Common.GE;
		constraints[2] = Common.LT;
		constraints[3] = Common.LE;
		constraints[4] = Common.EQ;
		
		list = new JComboBox(constraints);
		boolean oldAttrIsPath = isOldAttrPath(oldAttr);
		int ci;
		if (oldAttrIsPath)
			ci = parseConstraint(oldAttr);
		else
			ci = 0;
	    list.setSelectedIndex(ci);
	    int num;
	    if (oldAttrIsPath)
	    	num = parseNumber(ci, oldAttr);
	    else
	    	num = 0;
		spinner = new JSpinner(new SpinnerNumberModel(num, 0, Integer.MAX_VALUE, 1));
		spinner.setValue(new Integer(num));
		p = new JPanel(new BorderLayout(5, 5));
	    p.add(list, BorderLayout.WEST);
	    p.add(spinner, BorderLayout.CENTER);
	    message[0] = "Please set the path length:";
	    message[1] = p;
	    
	    options = new String[2];
	    options[0] = "OK";
	    options[1] = "Cancel";
	    
	    SwingUtilities.invokeLater(new Runnable() {
      		public void run() {
  				JFrame frame = adapter.getCySwingApplication().getJFrame();
      			int result = JOptionPane.showOptionDialog (
  						frame, //the parent that the dialog blocks
      			        message, //the dialog message array
      			        "Approximate Path Attribute", //the title of the dialog window
      			        JOptionPane.DEFAULT_OPTION, //option type
      			        JOptionPane.INFORMATION_MESSAGE, //message type
      			        null, //optional icon, use null to use the default icon
      			        options, //options string array, will be made into buttons
      			        options[0] //option that should be made into a default button
				);
      			
      			if (result == 0) { 
      				attribute = "?" + constraints[list.getSelectedIndex()] + ((Integer) spinner.getValue()).intValue();	
      				edgeRow.set(Common.EDGE_QUERY_ATTR, attribute);
      			}
      			else 
      				edgeRow.set(Common.EDGE_QUERY_ATTR, oldAttr);
      		}
		});
	}
	
	private boolean isOldAttrPath(String attr) {
		if (Common.isApproximatePath(oldAttr))
			return true;
		return false;
	}
	
	private int parseConstraint(String attr) {
		String c = attr.substring(1, 3);
		if(constraints[1].equals(c))
			return 1;
    	if(constraints[3].equals(c))
    		return 3;
	    c = attr.substring(1, 2);
	    if(constraints[0].equals(c))
	    	return 0;
    	if(constraints[2].equals(c))
    		return 2;
    	if(constraints[4].equals(c))
    		return 4;
    	return -1;
	}
	
	private int parseNumber(int constraintIndex, String attr) {
		int beginIndex = 1/*?*/ + constraints[constraintIndex].length();
		return (Integer.parseInt(attr.substring(beginIndex)));
	}
	
}
