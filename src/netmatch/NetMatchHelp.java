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

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.io.IOException;

public class NetMatchHelp extends JFrame implements ActionListener, WindowListener,HyperlinkListener, TreeSelectionListener {
  
  private final int WIDTH = 1000;
  private final int HEIGHT = 700;
  private JEditorPane editor;

  public static void main(String args[]) {
    NetMatchHelp f = new NetMatchHelp("NetMatch Help",null);
    f.setVisible(true);
  }

  public NetMatchHelp(String title,ImageIcon img) {
    setTitle(title);
    if(img != null)
      this.setIconImage(img.getImage());
    addWindowListener(this);
    getContentPane().setLayout(new BorderLayout());
    JPanel principale = new JPanel(new BorderLayout());
    principale.setPreferredSize(new Dimension(WIDTH,HEIGHT));
    JMenuBar menuBar = createMenuBar();
    principale.add(menuBar, BorderLayout.NORTH);

    JPanel left = new JPanel(new BorderLayout());
    left.add(createTree(), BorderLayout.CENTER);
    JPanel right = new JPanel(new BorderLayout());
    JScrollPane scroller = new JScrollPane();
    JViewport vp = scroller.getViewport();
    editor = createPage("/help/index.html");
    vp.add(editor);
    right.add(scroller, BorderLayout.CENTER); 

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    splitPane.setOneTouchExpandable(true);
    splitPane.setContinuousLayout(true);
    splitPane.setDividerLocation(200);
    principale.add(splitPane, BorderLayout.CENTER);
    getContentPane().add(principale, BorderLayout.CENTER);
    pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(screenSize.width / 2 - (getWidth() / 2), screenSize.height / 2 - (getHeight() / 2));
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenuItem i;
    JMenu fileMenu = new JMenu("File");
    i = new JMenuItem("Exit");
    i.addActionListener(this);
    fileMenu.add(i);
    menuBar.add(fileMenu);
    return menuBar;
  }

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if(command.equals("Exit")) {
      processEvent(new WindowEvent(this, 201));
    }
  }

  public void windowClosing(WindowEvent e) {
    if(e.getSource() != this)
      return;
    Window window = e.getWindow();
    if(window.equals(this)) {
      setVisible(false);
      dispose();
    }
  }

  public void windowActivated(WindowEvent e) {
  }

  public void windowDeactivated(WindowEvent e) {
  }

  public void windowDeiconified(WindowEvent e) {
  }

  public void windowClosed(WindowEvent e) {
  }

  public void windowIconified(WindowEvent e) {
  }

  public void windowOpened(WindowEvent e) {
  }

  private URL getPage(String page) {
    return getClass().getResource(page);
  }

  private JEditorPane createPage(String path) {
 	  URL url = getPage(path);
    try {
      if(url != null) {
        JEditorPane html = new JEditorPane(url);
        html.setEditable(false);
        html.addHyperlinkListener(this);
        return html;
      }
    }
    catch(IOException e) {
      System.err.println("Failed to laod:"+path);
    }
    return null;
  }

  public void hyperlinkUpdate(HyperlinkEvent e) {
    if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      if(e instanceof HTMLFrameHyperlinkEvent) {
        ((HTMLDocument)editor.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
      }
      else {
        try {
          editor.setPage(e.getURL());
        }
        catch (IOException ioe) {
          System.out.println("IOE: " + ioe);
        }
      }
    }
  }

  public JScrollPane createTree() {
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("NetMatch Help");
    DefaultMutableTreeNode introduction = new DefaultMutableTreeNode("Introduction");
    DefaultMutableTreeNode installation = new DefaultMutableTreeNode("Installation");
    DefaultMutableTreeNode usage = new DefaultMutableTreeNode("Usage");
    DefaultMutableTreeNode bug = new DefaultMutableTreeNode("Bugs report");
    top.add(introduction);
    top.add(installation);
    top.add(usage);
    top.add(bug);
    DefaultMutableTreeNode netMatchOptions = new DefaultMutableTreeNode("NetMatch* options");
    DefaultMutableTreeNode loadingInputData = new DefaultMutableTreeNode("Loading input data");
    DefaultMutableTreeNode drawingQuery = new DefaultMutableTreeNode("Drawing query");
    DefaultMutableTreeNode motifsLibrary = new DefaultMutableTreeNode("Motifs library");
    DefaultMutableTreeNode managingResults = new DefaultMutableTreeNode("Managing results");
    DefaultMutableTreeNode significance = new DefaultMutableTreeNode("Significance");
    usage.add(netMatchOptions);
    usage.add(loadingInputData);
    usage.add(drawingQuery);
    usage.add(motifsLibrary);
    usage.add(managingResults);
    usage.add(significance);
    JTree tree = new JTree(top) {
    
	public Insets getInsets() {
        return new Insets(5,5,5,5);
      }
    };
    tree.addTreeSelectionListener(this);
    return new JScrollPane(tree);
  }

  public void valueChanged(TreeSelectionEvent e) {
    String n = e.getPath().getLastPathComponent().toString();
    try {
      if(n.equals("NetMatch* Help")) {
        editor.setPage(getPage("/help/index.html"));
      }
      else if(n.equals("Introduction")) {
        editor.setPage(getPage("/help/introduction.html"));
      }
      else if(n.equals("Installation")) {
        editor.setPage(getPage("/help/installation.html"));
      }
      else if(n.equals("NetMatch* options")) {
        editor.setPage(getPage("/help/netmatch_options.html"));
      }
      else if(n.equals("Loading input data")) {
        editor.setPage(getPage("/help/loading_input_data.html"));
      }
      else if(n.equals("Drawing query")) {
        editor.setPage(getPage("/help/drawing_query.html"));
      }
      else if(n.equals("Motifs library")) {
        editor.setPage(getPage("/help/motifs_library.html"));
      }
      else if(n.equals("Managing results")) {
        editor.setPage(getPage("/help/managing_results.html"));
      }
      else if(n.equals("Significance")) {
        editor.setPage(getPage("/help/significance.html"));
      }
      else if(n.equals("Bugs report")) {
    	editor.setPage(getPage("/help/bugs_report.html"));
      }
    }
    catch(IOException ex) {
      System.err.println("Failed to load page!");
    }
  }
}