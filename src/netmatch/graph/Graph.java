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
package netmatch.graph;

import netmatch.algorithm.AttrComparator;

import static java.lang.Math.random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

@SuppressWarnings("unused")
public class Graph {
	private boolean oriented;
    private HashMap<Integer,Node> nodes;
    private Vector<Edge> edges;
    private int n;
    private Object[] attr; //Node attributes
    private int[] inCount; //Number of in edges for each node
    private int[] outCount; //Number of out edges for each node
    private int[][] in; //Nodes connected by in edges to each node
    private int[][] out; //Nodes connected by out edges to each node
    private Object[][] inAttr; //Edges attributes for in edges
    private Object[][] outAttr; //Edges attributes for out edges
    private AttrComparator nodeComparator;
    private AttrComparator edgeComparator;
    private int[] networkId;// CyNetwork id
    
    public Graph(GraphLoader loader, boolean oriented) throws Exception
    {
        nodes = new HashMap<Integer,Node>();
        edges = new Vector<Edge>();
        this.oriented = oriented;
        this.nodeComparator = null;
        this.edgeComparator = null;
        //n = nodes.size();
        n = loader.nodeCount();
        attr = new Object[n];
        int i, j, v = 0, size = 4 * n;
        for(i = 0; i < n; i++) {
        	Object attribute = loader.getNodeAttr(i);
        	this.addNode(i, attribute); 
        	attr[i] = attribute;
        }
        inCount = new int[n];
        outCount = new int[n];
        in = new int[n][];
        out = new int[n][];
        inAttr = new Object[n][];
        outAttr = new Object[n][];
        for(i = 0; i < n; i++) {
            int k = outCount[i] = loader.outEdgeCount(i);
            out[i] = new int[k];
            outAttr[i] = new Object[k];
            for(j = 0; j < k; j++) {
            	outAttr[i][j] = new Object();
            	int n2 = out[i][j] = loader.getOutEdge(i, j, outAttr);
                Object attribute = outAttr[i][j];
            	this.addEdge(i, n2, attribute);
            	inCount[n2]++;
            }
        }
        for(i = 0; i < n; i++) {
        	int k = inCount[i];
            in[i] = new int[k];
            inAttr[i] = new Object[k];
            int l = 0;
            for(j = 0 ; j < n; j++) {
            	if(hasEdge(j, i)) {
            		in[i][l] = j;
            		inAttr[i][l] = getEdgeAttr(j, i);
            		//this.addEdge(i, l);
            		l++;
            	}
            }
	    	if(l != k)
	    		throw new Exception();
        }
    	networkId = new int[n];
    	for(i = 0; i < n; i++) 
    		networkId[i] = loader.getCyNetworkID(i);
    }
    
    public Graph(HashMap<Integer,Node> nodes, Vector<Edge> edges, boolean oriented) {
    	this.nodes = nodes;
    	this.edges = edges;
    	this.oriented = oriented;
    }
    
    public Graph(boolean oriented) {
    	nodes = new HashMap<Integer,Node>();
        edges = new Vector<Edge>();
        this.oriented = oriented;
    }
    
    public boolean isEmpty()
    {
        return (nofNodes()==0);
    }
    
    public int nofNodes()
    {
        return nodes.size();
    }
    
    public HashMap<Integer,Node> nodes()
    {
        return nodes;
    }
    
    public Vector<Edge> edges()
    {
        return edges;
    }
    
    public int nofEdges()
    {
        return edges.size();
    }
    
    public void addNode(int id, Object attribute) 
    {
        Node x = new Node(id, attribute);
        nodes.put(id, x);
        //attr[id] = attribute;
    }
    
    public void addNode(int id) {
    	addNode(id, "?");
    }

    public void addEdge(int idSource, int idTarget) {
        addEdge(idSource, idTarget, null);
    }
    
    public void addEdge(int idSource, int idTarget, Object attr)
    {
        Node source = nodes.get(idSource);
        Node dest = nodes.get(idTarget);
        if(source!=null && dest!=null)
        {
            HashSet<Integer> outAdiac = source.getOutAdiacs();
            outAdiac.add(idTarget);
            HashSet<Integer> inAdiac = dest.getInAdiacs();
            inAdiac.add(idSource);
            edges.add(new Edge(idSource, idTarget, attr));
            if(!oriented)
            {
                outAdiac=dest.getOutAdiacs();
                outAdiac.add(idSource);
                inAdiac=source.getInAdiacs();
                inAdiac.add(idTarget);
                edges.add(new Edge(idTarget, idSource, attr));
            }
        }
        else
            System.out.println("Errore! Almeno uno dei due nodi non fa parte del grafo!");
    }
    
    public void removeEdge(int idSource, int idTarget)
	{
		Node source=nodes.get(idSource);
		Node dest=nodes.get(idTarget);
		if(source!=null && dest!=null)
		{
			HashSet<Integer> outAdiacs = source.getOutAdiacs();
			outAdiacs.remove(idTarget);
			HashSet<Integer> inAdiacs = dest.getInAdiacs();
			inAdiacs.remove(idSource);
			Iterator<Edge> iterator = edges.iterator();
			int count = 0;
			while(iterator.hasNext()) {
				Edge edge = iterator.next();
				int s = edge.getSource();
				int t = edge.getAdjTarget();
				if (s == idSource && t == idTarget)
					break;
				count++;
			}
			edges.remove(count);
			if(!oriented)
			{
				outAdiacs=dest.getOutAdiacs();
				outAdiacs.remove(idSource);
				inAdiacs=source.getInAdiacs();
				inAdiacs.remove(idTarget);
				count = 0;
				while(iterator.hasNext()) {
					Edge edge = iterator.next();
					int s = edge.getSource();
					int t = edge.getAdjTarget();
					if (s == idTarget && t == idSource)
						break;
					count++;
				}
				edges.remove(count);
			}
		}
		else
			System.out.println("Errore! Almeno uno dei due nodi non fa parte del grafo!");
	}
    
    public int[] getNodeIds() {
    	Set<Integer> nodesIds= nodes.keySet();
    	ArrayList<Integer> nodeIdsList = new ArrayList<Integer>(nodesIds);
    	int[] ids = new int[nodes.size()];
    	Iterator<Integer> iterator = nodeIdsList.iterator();
    	int i = 0;
    	while(iterator.hasNext()) {
    		ids[i++] = iterator.next();
    	}
    	
    	return ids;
    }
    
    public boolean isEdge(int idSource, int idTarget)
    {
        HashSet<Integer> outAdiac = nodes.get(idSource).getOutAdiacs();
        if(outAdiac.contains(idTarget))
            return true;
        else
            return false;
    }
    
    public int getNodeCount() {
    	return nodes.size();
    }
    
    public int getEdgeCount() {
    	return edges.size();
    }
    
    public void setNodeComparator(AttrComparator c) {
    	this.nodeComparator = c;
    }
    
    public void setEdgeComparator(AttrComparator c) {
    	this.edgeComparator = c;
    }
    
    /*public AttrComparator getNodeComparator() {
		return nodeComparator;
	}
	
	public AttrComparator getEdgeComparator() {
		return edgeComparator;
	}*/
    
    public boolean hasEdge(int n1, int n2, Object pattr) throws Exception {
    	int a, b, c;
    	int[] id = out[n1];

    	if(n1 < n && n2 < n) {
    		a = 0;
    		b = outCount[n1];
    		while(a < b) {
    			c = (a + b) >> 1;
            	if(id[c] < n2)
            		a = c + 1;
            	else if(id[c] > n2)
            		b = c;
            	else {
            		pattr = outAttr[n1][c];
        		return true;
            	}
    		}
    		return false;
    	}
    	throw new Exception();
    }
    
    public Object getNodeAttr(int i) throws Exception {
        if(i < n)
        	return attr[i];
        throw new Exception();
    }

    public boolean hasEdge(int n1, int n2) throws Exception {
        return hasEdge(n1, n2, null);
    }
    
    public Object getEdgeAttr(int n1, int n2) throws Exception {
        int a, b, c;
        int[] id = out[n1];
        if(n1 < n && n2 < n) {
        	a = 0;
        	b = outCount[n1];
        	while(a < b) {
        		c = (a + b) >> 1;
            	if(id[c] < n2)
            		a = c + 1;
            	else if(id[c] > n2)
            		b = c;
            	else
            		return outAttr[n1][c];
        	}
        	return null;
        }	
        throw new Exception();
    }
    
    public int inEdgeCount(int node) throws Exception {
        if(node < n)
        	return inCount[node];
        throw new Exception();
    }

    public int outEdgeCount(int node) throws Exception {
    	if(node < n)
    		return outCount[node];
        throw new Exception();
    }
    
    public int getInNode(int node, int i) throws Exception {
        if(node < n && i < inCount[node])
        	return in[node][i];
        throw new Exception();
    }

    public Edge getInEdge(int node, int i) throws Exception {
    	if(node < n && i < inCount[node])
    	{
    		int s = in[node][i];
    		int t = node;
    		int j;
    		for (j=0;j<out[s].length; j++)
    			if (out[s][j] == t)
    				return new Edge(s, j);
		}

    	throw new Exception();
    }
    
    public int getInEdge(int node, int i, Object pattr) throws Exception {
    	if(node < n && i < inCount[node]) {
    		pattr = inAttr[node][i];
    		return in[node][i];
    	}
    	throw new Exception();
    }

    public Object getInEdge2(int node, int i) throws Exception {
    	if(node < n && i < inCount[node])
    		return inAttr[node][i];
    	throw new Exception();
	}

    public int getOutNode(int node, int i) throws Exception {
    	if(node < n && i < outCount[node])
    		return out[node][i];
    	throw new Exception();
    }

    public Edge getOutEdge(int node, int i) throws Exception {
    	if(node < n && i < outCount[node])
    		return new Edge(node, i);
    	throw new Exception();
    }

    public int getOutEdge(int node, int i, Object pattr) throws Exception {
      if(node < n && i < outCount[node]) {
    	  pattr = outAttr[node][i];
    	  return out[node][i];
      }
      throw new Exception();
    }

    public Object getOutEdge2(int node, int i) throws Exception {
      if(node < n && i < outCount[node])
    	  return outAttr[node][i];
      throw new Exception();
    }

    public boolean compatibleNode(Object attr1, Object attr2) throws Exception {
    	if (nodeComparator == null) 
    		return true;
    	else
    		return nodeComparator.compatible(attr1, attr2);
    }
    
    public boolean compatibleEdge(Object attr1, Object attr2) throws Exception {
    	if (edgeComparator == null) 
    		return true;
    	else
    		return edgeComparator.compatible(attr1, attr2);
    }
    
    public int getOutDegree(int i)
    {
        return out[i].length;
    }
    
	private class EdgesBucket {
		private Edge[] edges;
		
		public EdgesBucket(Graph g) {
			edges = new Edge[g.getEdgeCount()];
			
			int k=0;
            for (int i=0; i<g.getNodeCount(); i++) {
                for (int j=0; j < g.getOutDegree(i); j++)
                    edges[k++] = new Edge(i,j);
            }
		}
		
		public Edge pickupEdge() {
			int i = (int) (random() * edges.length);
            return edges[i];
		}
	}
	
    public void degreePreservingShuffling(int count) throws Exception
    {
        // per grafi in cui la direzione dell'arco non conta (non diretti)

        // class ReverseEdge used as attribute of the reverse edges
        class ReverseEdge {
        }

        // add reverse edges
        for (int i = 0; i < n; i++)
        {
            int[] myout = out[i];
            Object[] myoutattr = outAttr[i];

            int degree = inCount[i] + outCount[i];
            out[i] = new int[degree];
            outAttr[i] = new Object[degree];
            int j,l,k;
            for (k = 0, j = 0,l = 0; (j < outCount[i]) && (l < inCount[i]); k++)
            {
                if (myout[j] <= in[i][l])
                {
                    out[i][k] = myout[j];
                    outAttr[i][k] = myoutattr[j];
                    j++;
                }
                else
                {
                    out[i][k] = in[i][l];
                    outAttr[i][k] = new ReverseEdge();
                    l++;
                }
            }
        }

        // swap edges
        int i=0;
        EdgesBucket b = new EdgesBucket (this);
        while(i < count)
        {
            Edge e1 = b.pickupEdge();
            Edge e2 = b.pickupEdge();

            if (e1.getSource() == e2.getSource() || e1.getTarget() == e2.getSource()
                  || e1.getSource() == e2.getTarget() || e1.getTarget() == e2.getTarget()
                  || hasEdge(e1.getSource(), e2.getTarget()) || hasEdge(e2.getSource(), e1.getTarget()))
            {
                i++;
                continue;
            }

            swapOutEdgesWithReverse(e1, e2);
            i++;
        }

        // remove reverse edges
        for (i=0; i<n; i++)
        {
            int j;
            int outdegree = 0;
            for (j = 0; j < out[i].length; j++)
                if (!(outAttr[i][j] instanceof ReverseEdge))
                    outdegree++;

            int myout[] = out[i];
            Object myoutattr[] = outAttr[i];

            out[i] = new int[outdegree];
            outAttr[i] = new Object[outdegree];

            int k = 0;
            for (j = 0; j < myout.length; j++)
                if (!(myoutattr[j] instanceof ReverseEdge))
                {
                    out[i][k] = myout[j];
                    outAttr[i][k] = myoutattr[j];
                    k++;
                }
        }

        sortOutEdges();
        allocateInEdges();
        adjustInEdges();
        sortInEdges();
    }

    public void inOutDegreePreservingShuffling(int count, boolean directed) throws Exception
    {
        int i=0;

        EdgesBucket b = new EdgesBucket (this);

        while(i<count)
        {
            Edge e1 = b.pickupEdge();
            Edge e2 = b.pickupEdge();

            if (e1.getSource() == e2.getSource() || e1.getTarget() == e2.getSource()
                  || e1.getSource() == e2.getTarget() || e1.getTarget() == e2.getTarget()
                  || hasEdge(e1.getSource(), e2.getTarget()) || hasEdge(e2.getSource(), e1.getTarget()))
            {
                i++;
                continue;
            }

            if (directed)
                swapOutEdges(e1,e2);
            else
                swapOutEdgesWithReverse(e1,e2); // keep the order
            i++;
        }

        if (!directed)
            sortOutEdges();
        adjustInEdges(); // non ha bisogno di riallocare perche' i gradi entranti sono conservati
        sortInEdges();
        
        /*
         * 
         */
        Iterator<Integer> nodesIterator = nodes.keySet().iterator();
        while(nodesIterator.hasNext()) {
        	Integer nodeId = nodesIterator.next();
        	Node node = nodes.get(nodeId);
        	node.inAdiacs.clear();
        	node.outAdiacs.clear();
        }
        edges.clear();
        for (i = 0; i < out.length; i++) {
        	for (int j = 0; j < out[i].length; j++)
        		addEdge(i, out[i][j]);
        }
        /*
         * 
         */
    }

    public void nodeLabelShuffling()
    {
        Object[] attrcopy = new Object[attr.length];
        for (int i=0; i<attr.length; i++)
            attrcopy[i] = attr[i];

        int length = attr.length;
        for (int i=0; i<attr.length; i++)
        {
            int k = (int) (random() * length);
            Node node = nodes.get(i);
            node.attribute = attrcopy[k];
            attr[i] = attrcopy[k];
            attrcopy[k] = attrcopy[--length];
        }
    }

    public void edgeLabelShuffling() throws Exception
    {
        Edge[] edges = new Edge[getEdgeCount()];
        Object[] labels = new Object[getEdgeCount()];

        // create the list of edges end lables
        int k=0;
        for (int i=0; i<getNodeCount(); i++)
        {
            for (int j=0; j<getOutDegree(i); j++)
            {
                edges[k] = new Edge(i,j);
                labels[k] = outAttr[i][j];
                k++;
            }
        }

        // fill out_attr by picking random edges from the list
        int length = edges.length;
        for (int i=0; i<edges.length; i++)
        {
            k = (int) (random() * length);
            Edge e = edges[k];
            Object l = labels[i];
            outAttr[e.source][e.adjTarget] = l;
            edges[k] = edges[--length];
        }

        // fill in_attr mirroring out_attr
        for (int i=0; i<n; i++)
        {
            for (int l=0; l<in[i].length; l++)
            {
                int j = in[i][l];
                inAttr[i][l] = getEdgeAttr(j, i);
            }
        }
    }

    private void swapOutEdges(Edge e1, Edge e2)
    {
        int t1 = e1.getTarget();
        int t2 = e2.getTarget();
        
        Object l1 = e1.getAttribute();
        Object l2 = e2.getAttribute();
        		
        // insert in ordered position
        int i;
        for (i=e1.getAdjTarget(); (i > 0) && (out[e1.getSource()][i-1] > t2); i--)
        {
            out[e1.getSource()][i] = out[e1.getSource()][i-1];
            outAttr[e1.getSource()][i] = outAttr[e1.getSource()][i-1];
        }
        for (; i<(outCount[e1.getSource()]-1) && (out[e1.getSource()][i+1] < t2); i++)
        {
            out[e1.getSource()][i] = out[e1.getSource()][i+1];
            outAttr[e1.getSource()][i] = outAttr[e1.getSource()][i+1];
        }
        out[e1.getSource()][i] = t2;
        outAttr[e1.getSource()][i] = l2;

        for (i=e2.getAdjTarget(); (i > 0) && (out[e2.getSource()][i-1] > t1); i--)
        {
            out[e2.getSource()][i] = out[e2.getSource()][i-1];
            outAttr[e2.getSource()][i] = outAttr[e2.getSource()][i-1];
        }
        for (; (i<outCount[e2.getSource()]-1) && (out[e2.getSource()][i+1] < t1); i++)
        {
            out[e2.getSource()][i] = out[e2.getSource()][i+1];
            outAttr[e2.getSource()][i] = outAttr[e2.getSource()][i+1];
        }
        out[e2.getSource()][i] = t1;
        outAttr[e2.getSource()][i] = l1;
    }

    private void swapOutEdgesWithReverse(Edge e1, Edge e2)
    {
        Edge er1 = e1.getReverseEdge();
        Edge er2 = e2.getReverseEdge();
        swapOutEdges(e1, e2);
        swapOutEdges(er1,er2);
    }

    private void sortOutEdges()
    {
        for (int i=0; i<n; i++)
        {
            Integer index[] = new Integer[outCount[i]];
            for (int j=0; j<outCount[i]; j++)
                index[j]=j;
            final int[] id = out[i];
            Comparator<Integer> itemComparator = new
               Comparator<Integer>()
               {
                  public int compare(Integer a, Integer b)
                  {
                     return id[a] - id[b];
                  }
               };
            Arrays.sort(index,itemComparator);
            int outcopy[] = new int [outCount[i]];
            Object outAttrcopy[] = new Object[outCount[i]];
            for (int j=0; j<outCount[i]; j++)
            {
                outcopy[j] = out[i][j];
                outAttrcopy[j] = outAttr[i][j];
            }
            for (int j=0; j<outCount[i]; j++)
            {
                out[i][j] = outcopy[index[j]];
                outAttr[i][j] = outAttrcopy[index[j]];
            }
        }
    }

    private void allocateInEdges()
    {
        int[] inDegree = new int [n];
        for (int i=0; i<n; i++)
            inDegree[i] = 0;

        for (int i=0; i<n; i++)
            for (int j=0; j<out[i].length; j++)
                inDegree[out[i][j]]++;

        for (int i=0; i<n; i++)
        {
            in[i] = new int[inDegree[i]];
            inAttr[i] = new Object[inDegree[i]];
        }
    }

    private void adjustInEdges()
    {
        int[] inAdj = new int [n];
        for (int i=0; i<n; i++)
            inAdj[i] = 0;

        for (int i=0; i<n; i++)
        {
            for (int j=0; j<out[i].length; j++)
            {
                int target = out[i][j];
                Object label = outAttr[i][j];

                in[target][inAdj[target]] = i;
                inAttr[target][inAdj[target]] = label;
                inAdj[target]++;
            }
        }
    }

    private void sortInEdges()
    {
        for (int i=0; i<n; i++)
        {
            Integer index[] = new Integer[inCount[i]];
            for (int j=0; j<inCount[i]; j++)
                index[j]=j;
            final int[] id = in[i];
            Comparator<Integer> itemComparator = new
               Comparator<Integer>()
               {
                  public int compare(Integer a, Integer b)
                  {
                     return id[a] - id[b];
                  }
               };
            Arrays.sort(index,itemComparator);
            int incopy[] = new int [inCount[i]];
            Object in_attrcopy[] = new Object[inCount[i]];
            for (int j=0; j<inCount[i]; j++)
            {
                incopy[j] = in[i][j];
                in_attrcopy[j] = inAttr[i][j];
            }
            for (int j=0; j<inCount[i]; j++)
            {
                in[i][j] = incopy[index[j]];
                inAttr[i][j] = in_attrcopy[index[j]];
            }
        }
    }
    
    public class Node
    {
        private int id;
        private Object attribute;
        public HashSet<Integer> inAdiacs;
        public HashSet<Integer> outAdiacs;
        
        public Node(int id, Object attribute)
        {
            this.id = id;
    	    this.attribute = attribute;
    	    inAdiacs = new HashSet<Integer>();
            outAdiacs = new HashSet<Integer>();
        }
        
        public int getId()
        {
            return id;
        }
        
        public Object getAttribute()
        {
            return attribute;
        }
        
        public HashSet<Integer> getInAdiacs()
        {
            return inAdiacs;
        }
        
        public HashSet<Integer> getOutAdiacs()
        {
            return outAdiacs;
        }
        
        public int inDegree()
        {
            return inAdiacs.size();
        }
        
        public int outDegree()
        {
            return outAdiacs.size();
        }
        
        public int totalDegree()
        {
            return inDegree()+outDegree();
        }
        
        public String toString()
        {
            return attribute.toString();
        }
    }
    
    public class Edge implements Comparable<Edge>
    {
        private int source;
        private int adjTarget;
        private Object attribute;
        
        public Edge(int source, int adjTarget)
        {
            this.source = source;
            this.adjTarget = adjTarget;
        }
        
        public Edge(int source, int adjTarget, Object attribute) {
        	this.source = source;
        	this.adjTarget = adjTarget;
        	this.attribute = attribute;
        }
        
        public int getSource()
        {
            return source;
        }
        
        public int getAdjTarget()
        {
            return adjTarget;
        }
        
        public int getTarget() {
        	return out[source][adjTarget];
        }
        
        public Object getAttribute() {
        	return outAttr[source][adjTarget];
        }
        
        public Edge getReverseEdge()
        {
            int n2 = source;
            int n1 = getTarget();

            int a, b, c;
            int[] id = out[n1];
            if(n1 < n && n2 < n) {
              a = 0;
              b = outCount[n1];
              while(a < b) {
                c = (a + b) >> 1;
                if(id[c] < n2)
                  a = c + 1;
                else if(id[c] > n2)
                  b = c;
                else
                  return new Edge(n1,c);
              }
              return null;
            }
            return null;
        }
        
        public String toString()
        {
            return "("+source+","+adjTarget+")";
        }
        
        public int compareTo(Edge edge) {
    		if (this.getSource() < edge.getSource()) 
    			return -1;
    		else if (this.getSource() == edge.getSource()) {
    			if (this.getAdjTarget() < edge.getAdjTarget()) 
    				return -1;
    			else if (this.getAdjTarget() == edge.getAdjTarget())
    				return 0;
    			else 
    				return 1;
    		}
    		else
    			return 1;
    	}
    }
}
