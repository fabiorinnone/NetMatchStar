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
package netmatch.main;

import javax.swing.*;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.io.IOException;

@SuppressWarnings("serial")
public class NetMatchQuickHelp extends JFrame implements ActionListener, WindowListener,HyperlinkListener {
  private final int WIDTH = 600;
  private final int HEIGHT = 500;
  private JEditorPane editor;

  public static void main(String args[]) {
    NetMatchQuickHelp f = new NetMatchQuickHelp("NetMatch Quick Help",null);
    f.setVisible(true);
  }

  public NetMatchQuickHelp(String title,ImageIcon img) {
    setTitle(title);
    if(img != null)
      this.setIconImage(img.getImage());
    addWindowListener(this);
    getContentPane().setLayout(new BorderLayout());
    JPanel princ = new JPanel(new BorderLayout());
    princ.setPreferredSize(new Dimension(WIDTH,HEIGHT));
    JScrollPane scroller = new JScrollPane();
    JViewport vp = scroller.getViewport();
    editor = createPage("/help/quick.html");
    vp.add(editor);
    princ.add(createMenuBar(),BorderLayout.NORTH);
    princ.add(scroller, BorderLayout.CENTER);
    getContentPane().add(princ,BorderLayout.CENTER);
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
}