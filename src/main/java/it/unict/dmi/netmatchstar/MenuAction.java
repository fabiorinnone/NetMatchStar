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
package it.unict.dmi.netmatchstar;

import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.JOptionPane;

import it.unict.dmi.netmatchstar.utils.Common;
import it.unict.dmi.netmatchstar.view.WestPanel;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;

@SuppressWarnings("serial")
public class MenuAction extends AbstractCyAction {
	//private final CyActivator cyActivator;

	private static CySwingAppAdapter adapter;
    private static boolean opened = false;

	public static void setOpened(boolean open) {
		opened = open;
	}

	public MenuAction(final String menuTitle, CyActivator activator, CySwingAppAdapter adapt) {
        super(menuTitle, activator.getcyApplicationManager(), null, null);
		//cyActivator = activator;
		adapter = adapt;
        setPreferredMenu("Apps");
    }
 
    public void actionPerformed(ActionEvent e) {
    	if(!opened) {
    		WestPanel panel = new WestPanel(adapter);
    		CyServiceRegistrar csr = adapter.getCyServiceRegistrar();
			csr.registerService(panel, CytoPanelComponent.class, new Properties());
			opened = true;
    	}
    	else {
    		JOptionPane.showMessageDialog(adapter.getCySwingApplication().getJFrame(),
					Common.APP_NAME + " is already open!");
    	}
    }

	public static CySwingAppAdapter getAdapter() {
		return adapter;
	}
}