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

import java.awt.event.*;
import java.awt.*;

@SuppressWarnings("serial")
public class JAboutDialog extends JWindow implements MouseListener/*, HyperlinkListener*/ {
  JEditorPane text;
  public JAboutDialog(WestPanel f, ImageIcon icon) {
    //setTitle("NetMatch About");
    //setResizable(false);
    JAboutPanel about = new JAboutPanel(icon);
    about.setLayout(null);
    addMouseListener(this);
    getContentPane().add(about, BorderLayout.CENTER);
    /*String s = "<p><font size=\"3\" face=\"Verdana\"><b>AUTHORS:</b><br><br>"+
            "<b>Gary Bader</b><br>" +
            "Banting and Best Department of Medical Research & Department of Medical" +
            "Genetics and Microbiology, University of Toronto, 160 College St," +
            "Toronto, Ontario, Canada M5S 3E1" +
            "<a href=\"mailto:gary.bader@utoronto.ca\">gary.bader@utoronto.ca</a><br><br>" +
            "<b>Alfredo Ferro</b><br>" +
            "Dept. of Mathematics and Computer Science, University of Catania<br>" +
            "<a href=\"mailto:ferro@dmi.unict.it\">ferro@dmi.unict.it</a><br><br>" +
            "<b>Rosalba Giugno</b><br>Dept. of Mathematics and Computer Science, University of Catania<br>" +
            "<a href=\"mailto:giugno@dmi.unict.it\">giugno@dmi.unict.it</a><br><br><b>Giuseppe Pigola</b><br>" +
            "Dept. of Mathematics and Computer Science, University of Catania<br>&nbsp;&nbsp;" +
            "<a href=\"mailto:pigola@dmi.unict.it\">pigola@dmi.unict.it</a><br><br><b>Alfredo Pulvirenti</b><br>" +
            "Dept. of Mathematics and Computer Science, University of Catania<br>" +
            "<a href=\"mailto:apulvirenti@dmi.unict.it\">apulvirenti@dmi.unict.it</a><br><br>"+
            "<b>Dennin Shasha</b><br>" +
            "Courant institute of Mathematical Science, New York University<br>" +
            "<a href=\"mailto:shasha@cs.nyu.edu\">shasha@cs.nyu.edu</a><br><br>" +
            "<b>Dimitry Skripin</b><br>" +
            "Dept. of Mathematics and Computer Science, University of Catania<br>" +
            "<a href=\"mailto:skripin@dmi.unict.it\">skripin@dmi.unict.it</a><br><br>"+
            "</font><font size=\"2\"></font>";

    HTMLEditorKit htmlKit = new HTMLEditorKit();
    HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
    try {
      htmlDoc.insertString(0,s,null);
    }
    catch(BadLocationException e) {
     //
    }
    text = new JEditorPane("text/html",s);
    text.setEditable(false);
    text.setBorder(new EtchedBorder());
    JScrollPane p = new JScrollPane();
    p.getViewport().add(text);
    p.setBounds(112,185,436,100);
    p.getVerticalScrollBar().setValue(p.getVerticalScrollBar().getMaximum());
    about.add(p);*/
    //p.getVerticalScrollBar().setValue(p.getVerticalScrollBar().getMaximum());
    pack();
    Rectangle r = f.getGraphicsConfiguration().getBounds();
    setLocation(r.x + r.width / 2 - getSize().width / 2, r.y + r.height / 2 - getSize().height / 2);
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {

  }

  public void mousePressed(MouseEvent e) {
    setVisible(false);
    dispose();
  }

  public void mouseReleased(MouseEvent e) {
  }

  /*public void hyperlinkUpdate(HyperlinkEvent e) {
    if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      if(e instanceof HTMLFrameHyperlinkEvent) {
        ((HTMLDocument)text.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
      }
      else {
        try {
          //text.setPage(e.getURL());

        }
        catch (IOException ioe) {
           System.out.println("IOE: " + ioe);
        }
      }
    }
  }*/
}
