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
package it.unict.dmi.netmatchstar.graph;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import it.unict.dmi.netmatchstar.CyActivator;
import it.unict.dmi.netmatchstar.MenuAction;
import it.unict.dmi.netmatchstar.utils.Common;

public class GraphLoader {
    private int count;
    private Node nodes;
    private Node lastNode;
    private Edge lastEdge;
    private CyActivator activator;
    private JPanel frame;

    public GraphLoader(CyActivator activator, JPanel frame) {
        count = 0;
        nodes = null;
        lastNode = null;
        lastEdge = null;
        this.activator = activator;
        this.frame = frame;
    }

    //Returns the number of nodes
    public int nodeCount() {
        return count;
    }

    //Returns the attr of a node
    public Object getNodeAttr(int id) {
        Node n = nodes;
        if(lastNode != null && lastNode.id <= id)
            n = lastNode;
        while(n != null && n.id != id)
            n = n.next;
        if(n == null) {
                if(frame != null)
                JOptionPane.showMessageDialog(
                        activator.getCySwingApplication().getJFrame(),
                        Common.APP_NAME + " Error. Inconsistent data!",
                        Common.APP_NAME + "Error", JOptionPane.ERROR_MESSAGE);
            else
                System.err.println(Common.APP_NAME + " Error. Inconsistent data!");
            return null;
        }
        return n.attr;
    }

    public int getCyNetworkID(int id) {
        Node n = nodes;
        if(lastNode != null && lastNode.id <= id)
            n = lastNode;
        while(n != null && n.id != id)
        n = n.next;
        if(n == null) {
            if(frame != null)
                JOptionPane.showMessageDialog(
                        activator.getCySwingApplication().getJFrame(),
                        Common.APP_NAME + " Error. Inconsistent data!",
                        Common.APP_NAME + "Error", JOptionPane.ERROR_MESSAGE);
            else
                System.err.println(Common.APP_NAME + " Error. Inconsistent data!");
            return -1;
        }
        return n.networkIndex;
    }

    //Returns the number of edges coming out of a node.
    public int outEdgeCount(int id) {
        Node n = nodes;
        if(lastNode != null && lastNode.id <= id)
             n = lastNode;
        while(n != null && n.id != id)
            n = n.next;
        if(n == null) {
            if(frame != null)
                JOptionPane.showMessageDialog(
        		        activator.getCySwingApplication().getJFrame(),
        		        Common.APP_NAME + "Error. Inconsistent data!", Common.APP_NAME + "Error",
                        JOptionPane.ERROR_MESSAGE);
            else
                System.err.println(Common.APP_NAME + "Error. Inconsistent data!");
            return -1;
        }
        return n.count;
    }

  //Returns an edge
    public int getOutEdge(int id, int i, Object[][] pattr) {
        Node n = nodes;
        if(lastNode != null && lastNode.id <= id)
            n = lastNode;
        while(n != null && n.id != id)
            n = n.next;
        if(n == null) {
            if(frame != null)
                JOptionPane.showMessageDialog(
        		        activator.getCySwingApplication().getJFrame(),
        		        Common.APP_NAME + "Error. Inconsistent data!", Common.APP_NAME + "Error",
                        JOptionPane.ERROR_MESSAGE);
            else
                System.err.println(Common.APP_NAME + "Error. Inconsistent data!");
            return -1;
        }
        Edge e = n.edges;
        int pos = 0;
        if(lastEdge != null && lastEdge.from == id && lastEdge.pos >= 0 && lastEdge.pos <= i) {
            e = lastEdge;
            pos = e.pos;
        }
        while(e != null && pos < i) {
            e.pos = pos;
            e = e.next;
            pos++;
        }
        if(e == null) {
            if(frame != null)
                JOptionPane.showMessageDialog(activator.getCySwingApplication().getJFrame(),
                        Common.APP_NAME + "Error. Inconsistent data!",
                        Common.APP_NAME + "Error", JOptionPane.ERROR_MESSAGE);
            else
                System.err.println(Common.APP_NAME + "Error. Inconsistent data!");
            return -1;
        }
        if(pattr != null)
            pattr[id][i] = e.attr;
        lastNode = n;
        lastEdge = e;
        return e.to;
    }

    //Creates a new node
    public int insertNode(Object attr, boolean isSelfEdge) {
        Node n = new Node();
        int id = n.id = count++;
        n.attr = attr;
        n.edges = null;
        n.count = 0;
        n.isSelfEdge = isSelfEdge;
        Node p = nodes, p0 = null;
        if(lastNode != null && lastNode.id < id) {
            p0 = lastNode;
            p = lastNode.next;
        }
        while(p != null && p.id < id) {
            p0 = p;
            p = p.next;
        }
        if(p0 == null) {
            n.next = nodes;
            nodes = n;
        }
        else {
            n.next = p0.next;
            p0.next = n;
        }
        lastNode = n;
        return n.id;
    }

    public int insertNode(Object attr, int networkIndex, boolean isSelfEdge) {
        Node n = new Node();
        int id = n.id = count++;
        n.attr = attr;
        n.edges = null;
        n.count = 0;
        n.networkIndex = networkIndex;
        n.isSelfEdge = isSelfEdge;
        Node p = nodes, p0 = null;
        if(lastNode != null && lastNode.id < id) {
            p0 = lastNode;
            p = lastNode.next;
        }
        while(p != null && p.id < id) {
            p0 = p;
            p = p.next;
        }
        if(p0 == null) {
            n.next = nodes;
            nodes = n;
        }
        else {
            n.next = p0.next;
           p0.next = n;
        }
        lastNode = n;
        return n.id;
    }

    //Creates a new edge
    public void insertEdge(int id1, int id2, Object attr, boolean isSelfEdge) {
        //System.out.println("DENTRO INSERT EDGE");
        Node pn = nodes;
        if(lastNode != null && lastNode.id <= id1)
            pn = lastNode;
        while(pn != null && pn.id < id1)
            pn = pn.next;
        if(pn == null || pn.id != id1)
            if(frame != null)
                JOptionPane.showMessageDialog(
        		        activator.getCySwingApplication().getJFrame(),
        		        Common.APP_NAME + "Warning. Bad param 1 in GraphLoader - InsertEdge: " + id1 + " " + id2,
        		        Common.APP_NAME + "Warning", JOptionPane.WARNING_MESSAGE);
            else
                System.err.println(Common.APP_NAME +
                        "Warning. Bad param 1 in GraphLoader - InsertEdge: " + id1 + " " + id2);
        else {
            Edge p = pn.edges, p0 = null;
            if(lastEdge != null && lastEdge.from == id1 && lastEdge.to < id2) {
                p0 = lastEdge;
                p = lastEdge.next;
            }
            while(p != null && p.to < id2) {
                p0 = p;
                p = p.next;
            }
            if(p != null && p.to == id2)
                p.update(attr);
            else {
                Edge e = new Edge(id1, id2, attr, isSelfEdge);
                if(p0 == null) {
                    e.next = pn.edges;
                   pn.edges = e;
                }
                else {
                    e.next = p0.next;
                    p0.next = e;
                }
                pn.count++;
                lastNode = pn;
                lastEdge = e;
            }
        }
    }
}