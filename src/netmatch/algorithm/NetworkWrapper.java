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
package netmatch.algorithm;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import giny.model.Edge;
import giny.model.Node;

public class NetworkWrapper {
    public NetworkWrapper(CyNetwork net)
    {
        m_net = net;
    }
  /*
    public void DegreePreservingShuffling(int n)
    {
        int i=0;

        // controllare che il grafo abbia distanza massima almeno 3 archi (altrimenti si blocca)

        // gestire il caso di grafo non diretto (scambiare anche i sorgenti)

        while(i<n)
        {
            CyEdge e1 = PickupEdge();
            CyEdge e2 = PickupEdge();

            if (e1.getSource()==e2.getSource() || e1.getTarget()==e2.getSource()
                  || e1.getSource()==e2.getTarget() || e1.getTarget()==e2.getTarget())
                continue;

            SwapEdges(e1,e2);
            i++;
        }
    }

    private CyEdge PickupEdge()
    {
        int nEdges = m_net.getEdgeCount();
        return
    }

    private void SwapEdges(CyEdge e1, CyEdge e2)
    {
        Node s = e1.getSource();
        Node t = e1.getTarget();
        Edge e = new CyEdge(s.getRootGraphIndex(),s.getRootGraphIndex(),"",false);
        CyEdge ce = m_net.addEdge(e);
        m_net.removeEdge(e1);
        e1.setTarget(e2.getTarget());




    }
 */
    private CyNetwork m_net;
}

