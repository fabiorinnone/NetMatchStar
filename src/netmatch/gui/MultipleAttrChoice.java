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

import netmatch.gui.WestPanel;
import netmatch.utils.Strings;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MultipleAttrChoice extends JDialog implements ActionListener {
  private JList source;
  private JList dest;
  private DefaultListModel destModel,sourceModel;
  private WestPanel panel;
  private boolean isNodeAttr;

  public MultipleAttrChoice(WestPanel panel,String title,JComboBox box,boolean isNodeAttr) {
    setTitle(title);
    setModal(true);
    this.panel = panel;
    this.isNodeAttr = isNodeAttr;
    getContentPane().setLayout(new BorderLayout(5,10));

    JPanel p = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();


    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0,0,0,0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 4;
    c.gridheight = 1;
    p.add(new JLabel("Available Attributes:"),c);

    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(0,10,0,5);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 4;
    c.gridheight = 5;
    source = new JList();
    source.setBorder(new EtchedBorder());
    sourceModel = new DefaultListModel();
    for(int i = 0;i < box.getItemCount();i++) {
      if(!box.getItemAt(i).toString().equals(Strings.LIST_ATTRIBUTES) &&
         !box.getItemAt(i).toString().equals(Strings.LIST_ATTRIBUTES_CHANGED))
        sourceModel.addElement(box.getItemAt(i).toString()) ;
    }
    source.setModel(sourceModel);
    source.setSelectedIndex(0);
    source.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    JScrollPane scroller1 = new JScrollPane();
    scroller1.setMinimumSize(new Dimension(300,300));
    scroller1.setPreferredSize(new Dimension(300,300));
    scroller1.setMaximumSize(new Dimension(300,300));
    scroller1.getViewport().add(source);
    p.add(scroller1,c);

    //p.add(source,c);

    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0,0,0,0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 5;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    JButton add = new JButton(">>");
    add.addActionListener(this);
    p.add(add,c);

    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0,0,0,0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 5;
    c.gridy = 4;
    c.gridwidth = 1;
    c.gridheight = 1;
    JButton remove = new JButton("<<");
    remove.addActionListener(this);
    p.add(remove,c);

    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0,0,0,0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 7;
    c.gridy = 0;
    c.gridwidth = 4;
    c.gridheight = 1;
    p.add(new JLabel("Selected Attributes:"),c);

    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(0,5,0,10);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 7;
    c.gridy = 1;
    c.gridwidth = 4;
    c.gridheight = 5;
    dest = new JList();
    dest.setBorder(new EtchedBorder());
    destModel = new DefaultListModel();
    dest.setModel(destModel);
    dest.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    JScrollPane scroller2 = new JScrollPane();
    scroller2.setMinimumSize(new Dimension(300,300));
    scroller2.setPreferredSize(new Dimension(300,300));
    scroller2.setMaximumSize(new Dimension(300,300));
    scroller2.getViewport().add(dest);
    p.add(scroller2,c);

    //p.add(dest,c);
    getContentPane().add(p,BorderLayout.CENTER);


    JButton ok = new JButton("OK");
    ok.addActionListener(this);
    ok.setPreferredSize(new Dimension(75,35));
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(ok);
    getContentPane().add(buttonPanel,BorderLayout.SOUTH);
    pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
  }

  private void swap(JList source, DefaultListModel s, DefaultListModel t) {
    Object[] obj = source.getSelectedValues();
    for(Object anObj : obj) {
      s.removeElement(anObj);
      t.addElement(anObj);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if(command.equals(">>")) {
      swap(source,sourceModel,destModel);
      if(!sourceModel.isEmpty())
        source.setSelectedIndex(0);
    }
    else if(command.equals("<<")) {
      swap(dest,destModel,sourceModel);
      if(!destModel.isEmpty())
        dest.setSelectedIndex(0);      
    }
    else {
      ArrayList attrs = null;
      //System.out.println("DEST MODEL SIZE:"+destModel.size());
      if(destModel.size() > 0) {
        attrs = new ArrayList();
        for(int i = 0;i < destModel.size();i++) {
          //System.out.println("ELEMENT:"+destModel.getElementAt(i).toString());
          attrs.add(destModel.getElementAt(i).toString());
        }
      }
      panel.setAttrList(attrs,isNodeAttr);
     // panel.wizardForm.wp2.setAttrList(attrs,isNodeAttr); //DA SISTEMARE
      setVisible(false);
      dispose();
    }
  }
}
