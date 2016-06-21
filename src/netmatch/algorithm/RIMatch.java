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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import netmatch.Common;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class RIMatch 
{
	private Graph pgraph;
	private Graph tgraph;
	private enum EdgeType {IN, OUT, UN};
	private long nofMatches;
	//private long[] nodesMatches;
	//private long nofDistinctMatches;
	//private long[] nodesMatchesDistinct;
	private ArrayList<int[]> sourcesList;
	private ArrayList<int[]> matchesList;
	private ArrayList<int[]> matchesList2;
	private Hashtable<String,Long> matchesOccurrences;
	private boolean isApproximate;
	private Vector qPaths;
	private ArrayList allPaths;
	private RIMatchListener matchListener = defaultMatchListener();
	private RIMatchListener distinctMatchListener = defaultMatchListener();
	
	public interface RIMatchListener {
		public void match(int[] targets); 
	}
	
	public static RIMatchListener defaultMatchListener(){
		return new RIMatchListener(){
			@Override
			public void match(int[] targets) {
				
			}
		};
	}
	
	public static RIMatchListener consoleMatchListener(){
		return new RIMatchListener(){
			@Override
			public void match(int[] targets) {
				System.out.print("(");
				for(int i=0;i<targets.length; i++){
					System.out.print(targets[i]);
					if(i<targets.length-1)
						System.out.print(",");
				}
				System.out.println(")");
			}
		};
	}
	
	public RIMatch(Graph patternGraph, Graph targetGraph)
	{
		this(patternGraph, targetGraph, RIMatch.defaultMatchListener(), RIMatch.defaultMatchListener());
		isApproximate = false;
		qPaths = null;
		allPaths = null;
	}
	
	public RIMatch(Graph patternGraph, Graph targetGraph, boolean isQueryApproximate, Vector paths) {
		this(patternGraph, targetGraph, RIMatch.defaultMatchListener(), RIMatch.defaultMatchListener());
		isApproximate = isQueryApproximate;
	    qPaths = paths;
	    allPaths = null;
	}
	
	public RIMatch(Graph patternGraph, Graph targetGraph, RIMatchListener riMatchListener, RIMatchListener riMatchListener2)
	{
		this.pgraph = patternGraph;
		this.tgraph = targetGraph;
		//this.nodesMatches = new long[tgraph.nodes().size()];
		//this.nodesMatchesDistinct = new long[tgraph.nodes().size()];
		this.sourcesList = new ArrayList<int[]>();
		this.matchesList = new ArrayList<int[]>();
		this.matchesList2 = new ArrayList<int[]>();
		this.matchesOccurrences = new Hashtable<String,Long>();
		this.matchListener = riMatchListener;
		this.distinctMatchListener = riMatchListener2;
		if(this.matchListener == null)
			this.matchListener = RIMatch.defaultMatchListener();
		if(this.distinctMatchListener == null)
			this.distinctMatchListener = RIMatch.defaultMatchListener();
	}
	
	public static int totalMatchingNodes(long[] matchesCount)
	{
		int count = 0;
		for(int i=0; i<matchesCount.length; i++)
		{
			if(matchesCount[i]>0)
				count++;
		}
		return count;
	}
    
	public void match() throws Exception
	{
		match(tgraph.nodes().keySet().iterator());
	}
	
	public void match(Iterator<Integer> startCandidates) throws Exception
	{
		long t_start = System.currentTimeMillis();
		 
		this.nofMatches = 0;
		//this.nodesMatches = new long[tgraph.nodes().size()];
		//this.nodesMatchesDistinct = new long[tgraph.nodes().size()];
		//this.nofDistinctMatches = 0;
		this.sourcesList = new ArrayList<int[]>();
		this.matchesList = new ArrayList<int[]>();
		this.matchesList2 = new ArrayList<int[]>();
		this.matchesOccurrences = new Hashtable<String,Long>();
		int nofStates = pgraph.nodes().size();
		EdgeType[] parentType = new EdgeType[nofStates];
		int[] parentState = new int[nofStates];			
		int[] patternNodes = new int[nofStates];	
		int[] siForPnode = new int[nofStates];
		BitSet[] domains = new BitSet[nofStates];
		int[] domainsSize = new int[nofStates];
		
		System.out.println("Initialization time "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
		fillDomains(domains, domainsSize);
		System.out.println("Filling domains time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
		buildMachingMachine(nofStates, parentType, parentState, patternNodes, siForPnode, -1, domainsSize);
		System.out.println("Building maching machine time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
		matchInternal(nofStates, parentType, parentState, patternNodes, siForPnode, startCandidates, domains);
		System.out.println("Matching time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
		
		//long total = this.nofMatches;
		
		int total;
		if (this.nofMatches > 0 && isApproximate) { //If there are matches and if the query contains a path
			ArrayList<int[]> l1 = sourcesList;
			ArrayList<int[]> l2 = matchesList;
			Hashtable<String,Long> t = new Hashtable<String,Long>();
	    	ArrayList<int[]> l3 = new ArrayList<int[]>();
	    	ArrayList<int[]> l4 = new ArrayList<int[]>();
	    	HashMap<String,String> map = new HashMap<String,String>();
	    	allPaths = new ArrayList();
	    	ArrayList paths = null;
	    	total = 0;
	    	for(int j = 0; j < l2.size();j++) {
	    		int tmp1[] = (int[])l1.get(j);
	    		int tmp2[] = (int[])l2.get(j);
	    		int count = 0;
	    		for(int i = 0; i < qPaths.size(); i++) {
	    			paths = new ArrayList();
	    			HashSet<Integer> nodes = new HashSet<Integer>();
	    			String p = (String)qPaths.elementAt(i);
	    			String vals[] = p.split(",");
	    			int tsource = tmp2[getIndexOf(l1.get(j), Integer.parseInt(vals[0]))];
	    			int tdest = tmp2[getIndexOf(l1.get(j), Integer.parseInt(vals[1]))];
	    			String tmp = vals[2].substring(1);
	    			String condition = tmp.substring(0,1);
	    			if(tmp.indexOf('=') != -1)
	    				condition = tmp.substring(0,tmp.indexOf('=')+1);
	    			int tmp3[] = null;
	    			if(tmp2.length > 2) {
	    				tmp3 = new int [tmp2.length-2];
	    				int k = 0;
	    				for(int h = 0;h < tmp2.length; h++)
	    					if(tmp2[h] != tsource && tmp2[h] != tdest) {
	    						tmp3[k] = tmp2[h];
	    						k++;
	    					}
	    			}
          
	    			Graph.Edge[] path = bfs(tmp3, tgraph, tsource, tdest, 
	    					condition, Integer.parseInt(tmp.substring(condition.length())), Common.DIRECTED);
	    			if(path != null) {
	    				paths.add(path);
	    				count++;
	    			}
	    			else
	    				break;
	    		}
	    		if(count == qPaths.size()) {
	    			HashSet<Integer> nodes = new HashSet<Integer>();
	    			Iterator<Graph.Edge[]> pathsIterator = paths.iterator();
	    			while(pathsIterator.hasNext()) {
	    				Graph.Edge[] path = pathsIterator.next();
	    				for (int i = 0; i < path.length; i++) {
	    					Graph.Edge edge = path[i];
	    					nodes.add(edge.getSource());
	    					nodes.add(edge.getTarget());
	    				}
	    			}
	    			for (int i = 0; i < tmp2.length; i++)
	    				nodes.add(tmp2[i]);
	    			String s1 = "";
	    			Iterator<Integer> nodesIterator = nodes.iterator();
	    			while(nodesIterator.hasNext()) {
	    				int node = nodesIterator.next();
	    				s1 += node;
	    				if (nodesIterator.hasNext())
	    					s1 += "-";
	    			}
	    			l3.add(tmp1);
	    			//l4.add(tmp2);
	    			total++;
	    			Arrays.sort(tmp2);
	    			String s2 = "";
	    			for (int i = 0; i < tmp2.length-1; i++)
	    				s2 += Integer.toString(tmp2[i])+"-";
	    			s2 += Integer.toString(tmp2[tmp2.length-1]);
	    			//if (!t.containsKey(s2)) { //Eventuale inserimento in l4
	    			if (!map.containsKey(s1)) {
	    				map.put(s1, s2);
	    				t.put (s2, new Long(1));
	    				l4.add(tmp2);
	    				allPaths.add(paths);
	    			}
	    			else {
	    				s2 = map.get(s1);
	    				t.put (s2, new Long(((Long)t.get(s2)).longValue()+1));
	    			}
	    			//allPaths.add(paths);
	            }
	    	}
	    	this.matchesList = l4;
	    	this.sourcesList = l3;
	    	this.matchesOccurrences = t;
	    	this.nofMatches = total;
	    	//this.nofDistinctMatches = l4.size();
		}
		System.out.println("Paths searching time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
	}
	
	private Graph.Edge[] bfs (int[] nodes, Graph tnetwork, int source, int dest, 
			String condition, int number, boolean directed) {
		if (source == dest)
			return null;
		if(number == 0 && condition.equals(Common.EQ))
			return null;
    
	    BfsPath bfsPath = new BfsPath();
	    List queue = new ArrayList();
	    queue.add(bfsPath);
	    boolean prune = isPruningPossible(condition);
	    while(!queue.isEmpty()) {
	    	bfsPath = (BfsPath)queue.get(0);
	    	queue.remove(0);
	    	int nodeId;
	    	if(bfsPath.isEmpty())
	    		nodeId = source;
	    	else {
	    		//int lastEdgeId = bfsPath.getLastEdge();
	    		EdgePair p = bfsPath.getLastEdge();
	    		Graph.Edge edge = p.getEdge();
	    		if(p.isOutGoingEdge())
	    			nodeId = edge.getTarget();
	    		else
	    			nodeId = edge.getSource();
	    	}
	    	if(prune && bfsPath.size() > number)
	    		return null;
	    	if(dest == nodeId && bfsPath.checkCondition(condition,number)) {
	    		return bfsPath.getApproximatePath();
	    	}
	    	try {
	    		//Outgoing edges for directed and undirected graphs
	    		for(int i = 0; i < tnetwork.outEdgeCount(nodeId); i++) {
	    			Graph.Edge edge = tnetwork.getOutEdge(nodeId ,i);
	    			int outNode = edge.getTarget();
	    			if(!bfsPath.contains(outNode) && isEligibleEdge(edge, nodes)) {
	    				BfsPath newBfsPath = new BfsPath(bfsPath);
	    				newBfsPath.add(edge,true);
	    				queue.add(newBfsPath);
	    			}
	    		}
	    		//Incoming edges only for undirected graphs
	    		if(!directed) {
	    			for(int i = 0; i < tnetwork.inEdgeCount(nodeId); i++) {
	    				Graph.Edge edge = tnetwork.getInEdge(nodeId, i);
	    				int inNode = edge.getSource();
	    				if(!bfsPath.contains(inNode) && isEligibleEdge(edge,nodes)) {
	    					BfsPath newBfsPath = new BfsPath(bfsPath);
	    					newBfsPath.add(edge, false);
	    					queue.add(newBfsPath);
	    				}
	    			}
	    		}
	    	} catch (Exception e) {
	    		return null;
	    	}
	    }
	    return null;
	}
	
	public boolean isEligibleEdge(Graph.Edge edge, int[] nonEligibleNodes) {
		if(nonEligibleNodes != null) {
			int s = edge.getSource();
			int d = edge.getTarget();
			for(int i = 0; i < nonEligibleNodes.length;i++) {
				if(nonEligibleNodes[i] == s || nonEligibleNodes[i] == d)
					return false;
			}
			return true;
		}
		return true;
	}
	
	private boolean isPruningPossible(String cond) {
		return cond.equals(Common.EQ) || cond.equals(Common.LT) || cond.equals(Common.LE);
	}
	
	private int getIndexOf(int[] array, int val) {
		for(int i = 0; i < array.length; i++)
			if(array[i] == val)
				return i;
		return -1;
	}
	
	private void matchInternal(int nofStates, EdgeType[] parentType, int[] parentState, int[] patternNodes,
	int[] siForPnode, Iterator<Integer> startCandidates, BitSet[] domains)
	{
		Set<MatchInst> matches = new TreeSet<MatchInst>();
		int[] matchedNodes = new int[nofStates];
		for(int i=0; i<nofStates; i++)
			matchedNodes[i] = -1;
		Iterator<Integer>[] candidates = (Iterator<Integer>[]) new Iterator[nofStates];
		candidates[0] = startCandidates;
		boolean[] alldiff=new boolean[tgraph.nodes().size()];
		int si = 0;
		int ci = -1;
		while(si!=-1)
		{
			if(matchedNodes[patternNodes[si]] !=-1)
				alldiff[matchedNodes[patternNodes[si]]] = false;
			ci = -1;
			while(candidates[si].hasNext())
			{
				ci = candidates[si].next();
				if(!alldiff[ci] && nodeMatch(si, ci, patternNodes, domains) && edgesMatch(si, ci, patternNodes, siForPnode, matchedNodes))
					break;
				else
					ci = -1;
			}
			if(ci == -1)
				si--;
			else
			{
				alldiff[ci] = true;
				matchedNodes[patternNodes[si]] = ci;
				if(si==nofStates-1)
				{
					matchListener.match(matchedNodes);
					nofMatches++;
					//for(int i=0;i<nofStates; i++)
						//nodesMatches[matchedNodes[i]]++;
					int[] match = Arrays.copyOf(matchedNodes, matchedNodes.length);
					int[] match2 = Arrays.copyOf(matchedNodes, matchedNodes.length);
					sourcesList.add(pgraph.getNodeIds());
					matchesList.add(match2);
					Arrays.sort(match);
					//print match
					/*System.out.print("[");
					for (int i = 0; i < match.length-1; i++)
						System.out.print(match[i]+",");
					System.out.println(match[match.length-1]+"]");*/
					String s = "";
					for (int i = 0; i < match.length-1; i++)
						s += Integer.toString(match[i])+"-";
					s += Integer.toString(match[match.length-1]);
					if (!matchesOccurrences.containsKey(s)) {
						matchesOccurrences.put(s, new Long(1));
						matchesList2.add(match);
					}
					else
						matchesOccurrences.put(s, new Long(((Long)matchesOccurrences.get(s)).longValue()+1));
					if(matches.add(new MatchInst(match)))
					{
						distinctMatchListener.match(matchedNodes);
						//nofDistinctMatches++;
						//for(int i=0;i<nofStates; i++)
							//nodesMatchesDistinct[matchedNodes[i]]++;
					}
				}
				else
				{
					matchedNodes[patternNodes[si+1]] = -1;
					if(parentType[patternNodes[si+1]] == null)
						candidates[si +1] = tgraph.nodes().keySet().iterator();
					else
					{
						switch(parentType[patternNodes[si +1]])
						{
							case IN:
								candidates[si +1] = tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getInAdiacs().iterator();
								break;
							case OUT:
								candidates[si +1] = tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getOutAdiacs().iterator();
								break;
							default:
							{
								Set<Integer> adiac=new TreeSet<Integer>();
								Set<Integer> outAdiac=tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getOutAdiacs();
								Set<Integer> inAdiac=tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getInAdiacs();
								if(outAdiac!=null)
									adiac.addAll(outAdiac);
								if(inAdiac!=null)
									adiac.addAll(inAdiac);
								candidates[si +1] = adiac.iterator();
							}
						} 
					}
					si++;
				}
			}
		}
	}
	
	private void fillDomains(BitSet[] domains, int[] domainsSize) throws Exception
	{
		for(int i=0; i<pgraph.nofNodes(); i++)
		{
			;//domains[i] = new BitSet(tgraph.nofNodes());
			domains[i] = new BitSet(0);
			for(int j=0; j<tgraph.nofNodes(); j++)
			{
				if(tgraph.nodes().get(j).inDegree()>=pgraph.nodes().get(i).inDegree() && tgraph.nodes().get(j).outDegree()>=pgraph.nodes().get(i).outDegree())
				{
					//if(pgraph.nodes().get(i).getAttribute().compareTo("?")==0 || pgraph.nodes().get(i).getAttribute().compareTo(tgraph.nodes().get(j).getAttribute())==0)
					//if(pgraph.nodes().get(i).compareTo(tgraph.nodes().get(j)) == 0)
					if (pgraph.compatibleNode(pgraph.nodes().get(i).getAttribute(), tgraph.nodes().get(j).getAttribute()))
						;//domains[i].set(j, true);
					else
						;//domains[i].set(j, false);
				}
				else
					;//domains[i].set(j, false);
			}
			;//domainsSize[i] = domains[i].cardinality();
		}
	}
	
	private boolean nodeMatch(int tnode,int pnode, BitSet[] domains)
	{
		return domains[pnode].get(tnode);
	}
	
	private boolean nodeMatch(int si, int tnode, int[] patternNodes, BitSet[] domains)
	{
		return nodeMatch(tnode, patternNodes[si], domains);
	}
    
	private boolean edgesMatch(int si, int tnode, int[] patternNodes, int[] siForPnode, int[] matchedNodes)
	{
		int pneigh;
		Iterator<Integer>  nIT = pgraph.nodes().get(patternNodes[si]).getInAdiacs().iterator();
		while(nIT.hasNext())
		{
			pneigh = nIT.next();
			if(siForPnode[pneigh] < si)
			{
				try {
					Object pAttr = pgraph.getEdgeAttr(pneigh, patternNodes[si]);
					Object tAttr = tgraph.getEdgeAttr(matchedNodes[pneigh], tnode);
					if (tgraph.isEdge(matchedNodes[pneigh], tnode)) {
						if (!tgraph.compatibleEdge(pAttr, tAttr))
							return false;
					}
					else return false;
				}
				catch(Exception e) {
					return false;
				}
			}
			else if(siForPnode[pneigh] == si)
			{
				try {
					Object pAttr = pgraph.getEdgeAttr(pneigh, patternNodes[si]);
					Object tAttr = tgraph.getEdgeAttr(matchedNodes[pneigh], tnode);
					if (!tgraph.isEdge(tnode, tnode)) {
						if (!tgraph.compatibleEdge(pAttr, tAttr))
							return false;
					}
					else return false;
				}
				catch (Exception e) {
					return false;
				}
			}
		}
		nIT = pgraph.nodes().get(patternNodes[si]).getOutAdiacs().iterator();
		while(nIT.hasNext())
		{
			pneigh = nIT.next();
			if(siForPnode[pneigh] < si)
			{
				try {
					Object pAttr = pgraph.getEdgeAttr(patternNodes[si], pneigh);
					Object tAttr = tgraph.getEdgeAttr(tnode, matchedNodes[pneigh]);
					if (tgraph.isEdge(tnode, matchedNodes[pneigh])) {
						if (!tgraph.compatibleEdge(pAttr, tAttr))
							return false;
					}
					else return false;
				}
				catch (Exception e) {
					return false;
				}
			}
			else if(siForPnode[pneigh] == si)
			{
				try {
					Object pAttr = pgraph.getEdgeAttr(patternNodes[si], pneigh);
					Object tAttr = tgraph.getEdgeAttr(tnode, matchedNodes[pneigh]);
					if (tgraph.isEdge(tnode, tnode)) {
						if (!tgraph.compatibleEdge(pAttr, tAttr))
							return false;
					}
					else return false;
				}
				catch (Exception e) {
					return false;
				}
			}
		}
		return true;
	}
	
	private enum NodeFlag {NS_CORE, NS_CNEIGH, NS_UNV};
	
	private int wcompare(int i, int j, int[][] weights, int[] domainsSize)
	{
		if(domainsSize[i] == 1)
			return -1;
		if(domainsSize[j] == 1)
			return 1;
		for(int w=0; w<3; w++)
		{
			if(weights[i][w] != weights[j][w])
				return weights[j][w] - weights[i][w];
		}
		return i-j;
	}
	
	private void buildMachingMachine(int nofStates, EdgeType[] parentType, int[] parentState, int[] patternNodes, int[] siForPnode, 
	int firstNode, int[] domainsSize)
	{
		int[] parentNode = new int[nofStates];
		boolean[] visited = new boolean[nofStates];
		for(int i=0; i<visited.length; i++) 
			visited[i] = false;
		int si = 0;
		Iterator<Integer> nIT, nnIT; 
		int ni, nni;
		LinkedList<Integer> nqueue = new LinkedList<Integer>();
		int[][] weights = new int[nofStates][3];//core, core_neighs, degree
		NodeFlag[] nodeFlags = new NodeFlag[nofStates];
		for(int i=0; i<nofStates; i++)
		{
			nodeFlags[i] = NodeFlag.NS_UNV;
			weights[i][0] = 0;
			weights[i][1] = 0;
			weights[i][2] = pgraph.nodes().get(i).totalDegree();
		}
		int sdnodes = 0;
		if(firstNode>=0)
		{
			nqueue.push(firstNode);
			parentType[firstNode] = null;
			parentNode[firstNode] = -1;
			visited[firstNode] = true;
			nIT = pgraph.nodes().get(firstNode).getInAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(ni != firstNode)
					weights[ni][1]++;
			}
			nIT = pgraph.nodes().get(firstNode).getOutAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(ni != firstNode)
				{
					if(! pgraph.nodes().get(firstNode).getInAdiacs().contains(ni))
						weights[ni][1]++;
				}
			}
		}
		while(si != nofStates)
		{
			int n;
			int nqi;
			if(nqueue.isEmpty())
			{
				if(si!=0)
					System.out.println("Warning: query is disconnected");
				int maxn = -1;
				int maxv = -1;
				int m=0;
				for(; m< nofStates; m++)
				{
					if(!visited[m])
					{
						if(maxn == -1)
						{
							maxn = m;
							maxv = weights[m][2];
						}
						else
						{
							if(weights[m][2] > maxv)
							{
								maxn = m;
								maxv = weights[m][2];
							}
						}
					}
				}
				n = maxn;
				nqi = 0;
				nqueue.push(n);
				parentType[n] = null;
				parentNode[n] = -1;
				visited[n] = true;
				nIT = pgraph.nodes().get(n).getInAdiacs().iterator();
				while(nIT.hasNext())
				{
					ni = nIT.next();
					if(ni != n)
						weights[ni][1]++;
				}
				nIT = pgraph.nodes().get(n).getOutAdiacs().iterator();
				while(nIT.hasNext())
				{
					ni = nIT.next();
					if(ni != n)
					{
						if(! pgraph.nodes().get(n).getInAdiacs().contains(ni))
							weights[ni][1]++;
					}
				}
			}
			else
			{
				int maxn = -1;
				int maxnqi = -1;
				int maxv = -1;
				int mi=0;
				int m;
				for(;mi<nqueue.size(); mi++)
				{
					m = nqueue.get(mi);
					if(maxn == -1)
					{
						maxn = m;
						maxnqi = mi;
						maxv = weights[m][2];
					}
					else
					{
						if(wcompare(m, maxn, weights, domainsSize) < 0)
						{
							maxn = m;
							maxnqi = mi;
							maxv = weights[m][2];
						}
					}
				}
				n = maxn;
				nqi = maxnqi;
			}
			nqueue.remove(nqi);
			nodeFlags[n] = NodeFlag.NS_CORE;
			patternNodes[si] = n;
			siForPnode[n] = si;
			nIT = pgraph.nodes().get(n).getInAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(visited[ni] == false)
				{
					parentType[ni] = EdgeType.IN;
					parentNode[ni] = n;
					visited[ni] = true;
					nqueue.push(ni);
				}
				if(ni != n)
				{
					weights[ni][0]++;
					weights[ni][1]--;
					if(nodeFlags[ni] == NodeFlag.NS_UNV)
					{
						nodeFlags[ni] = NodeFlag.NS_CNEIGH;
						nnIT = pgraph.nodes().get(ni).getInAdiacs().iterator();
						while(nnIT.hasNext())
						{
							nni = nnIT.next();
							weights[nni][1]++;
						}
						nnIT = pgraph.nodes().get(ni).getOutAdiacs().iterator();
						while(nnIT.hasNext())
						{
							nni = nnIT.next();
							if(! pgraph.nodes().get(ni).getInAdiacs().contains(nni))
							weights[nni][1]++;
						}
					}
				}
			}
			nIT = pgraph.nodes().get(n).getOutAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(visited[ni] == false)
				{
					parentType[ni] = EdgeType.OUT;
					parentNode[ni] = n;
					visited[ni] = true;
					nqueue.push(ni);
				}
				if(ni != n)
				{
					if(!pgraph.nodes().get(n).getInAdiacs().contains(ni))
					{
						weights[ni][0]++;
						weights[ni][1]--;
						if(nodeFlags[ni] == NodeFlag.NS_UNV)
						{
							nodeFlags[ni] = NodeFlag.NS_CNEIGH;
							nnIT = pgraph.nodes().get(ni).getInAdiacs().iterator();
							while(nnIT.hasNext())
							{
								nni = nnIT.next();
								weights[nni][1]++;
							}
							nnIT = pgraph.nodes().get(ni).getOutAdiacs().iterator();
							while(nnIT.hasNext())
							{
								nni = nnIT.next();
								if(!pgraph.nodes().get(ni).getInAdiacs().contains(nni))
									weights[nni][1]++;
							}
						}
					}
				}
			}
			si++;
		}
		for(int i=0; i<nofStates; i++)
		{
			if(parentNode[i] == -1)
				parentState[siForPnode[i]] = -1;
			else
				parentState[siForPnode[i]] = siForPnode[parentNode[i]];
		}
	}
	
	public long getNofMatches() {
		return nofMatches;
	}
	
	/*public long[] getNodesMatches() {
		return nodesMatches;
	}
	
	public long getNofDistinctMatches() {
		return nofDistinctMatches;
	}
	
	public long[] getNodesMatchesDistinct() {
		return nodesMatchesDistinct;
	}*/
	
	public ArrayList<int[]> getSourcesList() {
		return sourcesList;
	}
	
	public ArrayList<int[]> getMatchesList() {
		return matchesList;
	}
	
	public ArrayList<int[]> getMatchesList2() {
		return matchesList2;
	}
	
	public Hashtable<String,Long> getMatchesOccurrences() {
		return matchesOccurrences;
	}
	
	public ArrayList getApproximatePaths() {
		return allPaths;
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public void match_simple() throws Exception
	{
		match_simple(tgraph.nodes().keySet().iterator());
	}
	
	public void match_simple(Iterator<Integer> startCandidates) throws Exception
	{
		long t_start = System.currentTimeMillis();
		 
		this.nofMatches = 0;
		//this.nodesMatches = new long[tgraph.nodes().size()];
		//this.nodesMatchesDistinct = new long[tgraph.nodes().size()];
		//this.nofDistinctMatches = 0;
		this.sourcesList = new ArrayList<int[]>();
		this.matchesList = new ArrayList<int[]>();
		this.matchesList2 = new ArrayList<int[]>();
		this.matchesOccurrences = new Hashtable<String,Long>();
		int nofStates = pgraph.nodes().size();
		EdgeType[] parentType = new EdgeType[nofStates];
		int[] parentState = new int[nofStates];			
		int[] patternNodes = new int[nofStates];	
		int[] siForPnode = new int[nofStates];
		
		System.out.println("Initialization time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
		buildMachingMachine_simple(nofStates, parentType, parentState, patternNodes, siForPnode, -1);
		System.out.println("Building maching machine time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
		matchInternal_simple(nofStates, parentType, parentState, patternNodes, siForPnode, startCandidates);
		System.out.println("Matching time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
		
		//long total = this.nofMatches;
		
		int total;
		if (this.nofMatches > 0 && isApproximate) { //If there are matches and if the query contains a path
			ArrayList<int[]> l1 = sourcesList;
			ArrayList<int[]> l2 = matchesList;
			Hashtable<String,Long> t = new Hashtable<String,Long>();
	    	ArrayList<int[]> l3 = new ArrayList<int[]>();
	    	ArrayList<int[]> l4 = new ArrayList<int[]>();
	    	HashMap<String,String> map = new HashMap<String,String>();
	    	allPaths = new ArrayList();
	    	ArrayList paths = null;
	    	total = 0;
	    	for(int j = 0; j < l2.size();j++) {
	    		int tmp1[] = (int[])l1.get(j);
	    		int tmp2[] = (int[])l2.get(j);
	    		int count = 0;
	    		for(int i = 0; i < qPaths.size(); i++) {
	    			paths = new ArrayList();
	    			HashSet<Integer> nodes = new HashSet<Integer>();
	    			String p = (String)qPaths.elementAt(i);
	    			String vals[] = p.split(",");
	    			int tsource = tmp2[getIndexOf(l1.get(j), Integer.parseInt(vals[0]))];
	    			int tdest = tmp2[getIndexOf(l1.get(j), Integer.parseInt(vals[1]))];
	    			String tmp = vals[2].substring(1);
	    			String condition = tmp.substring(0,1);
	    			if(tmp.indexOf('=') != -1)
	    				condition = tmp.substring(0,tmp.indexOf('=')+1);
	    			int tmp3[] = null;
	    			if(tmp2.length > 2) {
	    				tmp3 = new int [tmp2.length-2];
	    				int k = 0;
	    				for(int h = 0;h < tmp2.length; h++)
	    					if(tmp2[h] != tsource && tmp2[h] != tdest) {
	    						tmp3[k] = tmp2[h];
	    						k++;
	    					}
	    			}
          
	    			Graph.Edge[] path = bfs(tmp3, tgraph, tsource, tdest, 
	    					condition, Integer.parseInt(tmp.substring(condition.length())), Common.DIRECTED);
	    			if(path != null) {
	    				paths.add(path);
	    				count++;
	    			}
	    			else
	    				break;
	    		}
	    		if(count == qPaths.size()) {
	    			HashSet<Integer> nodes = new HashSet<Integer>();
	    			Iterator<Graph.Edge[]> pathsIterator = paths.iterator();
	    			while(pathsIterator.hasNext()) {
	    				Graph.Edge[] path = pathsIterator.next();
	    				for (int i = 0; i < path.length; i++) {
	    					Graph.Edge edge = path[i];
	    					nodes.add(edge.getSource());
	    					nodes.add(edge.getTarget());
	    				}
	    			}
	    			for (int i = 0; i < tmp2.length; i++)
	    				nodes.add(tmp2[i]);
	    			String s1 = "";
	    			Iterator<Integer> nodesIterator = nodes.iterator();
	    			while(nodesIterator.hasNext()) {
	    				int node = nodesIterator.next();
	    				s1 += node;
	    				if (nodesIterator.hasNext())
	    					s1 += "-";
	    			}
	    			l3.add(tmp1);
	    			//l4.add(tmp2);
	    			total++;
	    			Arrays.sort(tmp2);
	    			String s2 = "";
	    			for (int i = 0; i < tmp2.length-1; i++)
	    				s2 += Integer.toString(tmp2[i])+"-";
	    			s2 += Integer.toString(tmp2[tmp2.length-1]);
	    			//if (!t.containsKey(s2)) { //Eventuale inserimento in l4
	    			if (!map.containsKey(s1)) {
	    				map.put(s1, s2);
	    				t.put (s2, new Long(1));
	    				l4.add(tmp2);
	    				allPaths.add(paths);
	    			}
	    			else {
	    				s2 = map.get(s1);
	    				t.put (s2, new Long(((Long)t.get(s2)).longValue()+1));
	    			}
	    			//allPaths.add(paths);
	            }
	    	}
	    	this.matchesList = l4;
	    	this.sourcesList = l3;
	    	this.matchesOccurrences = t;
	    	this.nofMatches = total;
	    	//this.nofDistinctMatches = l4.size();
		}
		System.out.println("Paths searching time: "+(((double)(System.currentTimeMillis()-t_start))/(1000.0)));
	}
	
	private void buildMachingMachine_simple(int nofStates, EdgeType[] parentType, int[] parentState, int[] patternNodes, int[] siForPnode,  int firstNode)
	{
		int[] parentNode = new int[nofStates];
		boolean[] visited = new boolean[nofStates];
		for(int i=0; i<visited.length; i++) 
			visited[i] = false;
		int si = 0;
		Iterator<Integer> nIT, nnIT; 
		int ni, nni;
		LinkedList<Integer> nqueue = new LinkedList<Integer>();
		int[][] weights = new int[nofStates][3];//core, core_neighs, degree
		NodeFlag[] nodeFlags = new NodeFlag[nofStates];
		for(int i=0; i<nofStates; i++)
		{
			nodeFlags[i] = NodeFlag.NS_UNV;
			weights[i][0] = 0;
			weights[i][1] = 0;
			weights[i][2] = pgraph.nodes().get(i).totalDegree();
		}
		int sdnodes = 0;
		if(firstNode>=0)
		{
			nqueue.push(firstNode);
			parentType[firstNode] = null;
			parentNode[firstNode] = -1;
			visited[firstNode] = true;
			nIT = pgraph.nodes().get(firstNode).getInAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(ni != firstNode)
					weights[ni][1]++;
			}
			nIT = pgraph.nodes().get(firstNode).getOutAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(ni != firstNode)
				{
					if(! pgraph.nodes().get(firstNode).getInAdiacs().contains(ni))
						weights[ni][1]++;
				}
			}
		}
		while(si != nofStates)
		{
			int n;
			int nqi;
			if(nqueue.isEmpty())
			{
				if(si!=0)
					System.out.println("Warning: query is disconnected");
				int maxn = -1;
				int maxv = -1;
				int m=0;
				for(; m< nofStates; m++)
				{
					if(!visited[m])
					{
						if(maxn == -1)
						{
							maxn = m;
							maxv = weights[m][2];
						}
						else
						{
							if(weights[m][2] > maxv)
							{
								maxn = m;
								maxv = weights[m][2];
							}
						}
					}
				}
				n = maxn;
				nqi = 0;
				nqueue.push(n);
				parentType[n] = null;
				parentNode[n] = -1;
				visited[n] = true;
				nIT = pgraph.nodes().get(n).getInAdiacs().iterator();
				while(nIT.hasNext())
				{
					ni = nIT.next();
					if(ni != n)
						weights[ni][1]++;
				}
				nIT = pgraph.nodes().get(n).getOutAdiacs().iterator();
				while(nIT.hasNext())
				{
					ni = nIT.next();
					if(ni != n)
					{
						if(! pgraph.nodes().get(n).getInAdiacs().contains(ni))
							weights[ni][1]++;
					}
				}
			}
			else
			{
				int maxn = -1;
				int maxnqi = -1;
				int maxv = -1;
				int mi=0;
				int m;
				for(;mi<nqueue.size(); mi++)
				{
					m = nqueue.get(mi);
					if(maxn == -1)
					{
						maxn = m;
						maxnqi = mi;
						maxv = weights[m][2];
					}
					else
					{
						if(wcompare_simple(m, maxn, weights) < 0)
						{
							maxn = m;
							maxnqi = mi;
							maxv = weights[m][2];
						}
					}
				}
				n = maxn;
				nqi = maxnqi;
			}
			nqueue.remove(nqi);
			nodeFlags[n] = NodeFlag.NS_CORE;
			patternNodes[si] = n;
			siForPnode[n] = si;
			nIT = pgraph.nodes().get(n).getInAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(visited[ni] == false)
				{
					parentType[ni] = EdgeType.IN;
					parentNode[ni] = n;
					visited[ni] = true;
					nqueue.push(ni);
				}
				if(ni != n)
				{
					weights[ni][0]++;
					weights[ni][1]--;
					if(nodeFlags[ni] == NodeFlag.NS_UNV)
					{
						nodeFlags[ni] = NodeFlag.NS_CNEIGH;
						nnIT = pgraph.nodes().get(ni).getInAdiacs().iterator();
						while(nnIT.hasNext())
						{
							nni = nnIT.next();
							weights[nni][1]++;
						}
						nnIT = pgraph.nodes().get(ni).getOutAdiacs().iterator();
						while(nnIT.hasNext())
						{
							nni = nnIT.next();
							if(! pgraph.nodes().get(ni).getInAdiacs().contains(nni))
							weights[nni][1]++;
						}
					}
				}
			}
			nIT = pgraph.nodes().get(n).getOutAdiacs().iterator();
			while(nIT.hasNext())
			{
				ni = nIT.next();
				if(visited[ni] == false)
				{
					parentType[ni] = EdgeType.OUT;
					parentNode[ni] = n;
					visited[ni] = true;
					nqueue.push(ni);
				}
				if(ni != n)
				{
					if(!pgraph.nodes().get(n).getInAdiacs().contains(ni))
					{
						weights[ni][0]++;
						weights[ni][1]--;
						if(nodeFlags[ni] == NodeFlag.NS_UNV)
						{
							nodeFlags[ni] = NodeFlag.NS_CNEIGH;
							nnIT = pgraph.nodes().get(ni).getInAdiacs().iterator();
							while(nnIT.hasNext())
							{
								nni = nnIT.next();
								weights[nni][1]++;
							}
							nnIT = pgraph.nodes().get(ni).getOutAdiacs().iterator();
							while(nnIT.hasNext())
							{
								nni = nnIT.next();
								if(!pgraph.nodes().get(ni).getInAdiacs().contains(nni))
									weights[nni][1]++;
							}
						}
					}
				}
			}
			si++;
		}
		for(int i=0; i<nofStates; i++)
		{
			if(parentNode[i] == -1)
				parentState[siForPnode[i]] = -1;
			else
				parentState[siForPnode[i]] = siForPnode[parentNode[i]];
		}
	}
	
	private int wcompare_simple(int i, int j, int[][] weights)
	{
		for(int w=0; w<3; w++)
		{
			if(weights[i][w] != weights[j][w])
				return weights[j][w] - weights[i][w];
		}
		return i-j;
	}

	private void matchInternal_simple(int nofStates, EdgeType[] parentType, int[] parentState, int[] patternNodes, int[] siForPnode, Iterator<Integer> startCandidates)
	{
		Set<MatchInst> matches = new TreeSet<MatchInst>();
		int[] matchedNodes = new int[nofStates];
		for(int i=0; i<nofStates; i++)
			matchedNodes[i] = -1;
		Iterator<Integer>[] candidates = (Iterator<Integer>[]) new Iterator[nofStates];
		candidates[0] = startCandidates;
		boolean[] alldiff=new boolean[tgraph.nodes().size()];
		int si = 0;
		int ci = -1;
		while(si!=-1)
		{
			if(matchedNodes[patternNodes[si]] !=-1)
				alldiff[matchedNodes[patternNodes[si]]] = false;
			ci = -1;
			while(candidates[si].hasNext())
			{
				ci = candidates[si].next();
				if(!alldiff[ci] && nodeMatch_simple(si, ci, patternNodes) && edgesMatch(si, ci, patternNodes, siForPnode, matchedNodes))
					break;
				else
					ci = -1;
			}
			if(ci == -1)
				si--;
			else
			{
				alldiff[ci] = true;
				matchedNodes[patternNodes[si]] = ci;
				if(si==nofStates-1)
				{
					matchListener.match(matchedNodes);
					nofMatches++;
					//for(int i=0;i<nofStates; i++)
						//nodesMatches[matchedNodes[i]]++;
					int[] match = Arrays.copyOf(matchedNodes, matchedNodes.length);
					int[] match2 = Arrays.copyOf(matchedNodes, matchedNodes.length);
					sourcesList.add(pgraph.getNodeIds());
					matchesList.add(match2);
					Arrays.sort(match);
					//print match
					/*System.out.print("[");
					for (int i = 0; i < match.length-1; i++)
						System.out.print(match[i]+",");
					System.out.println(match[match.length-1]+"]");*/
					String s = "";
					for (int i = 0; i < match.length-1; i++)
						s += Integer.toString(match[i])+"-";
					s += Integer.toString(match[match.length-1]);
					if (!matchesOccurrences.containsKey(s)) {
						matchesOccurrences.put(s, new Long(1));
						matchesList2.add(match);
					}
					else
						matchesOccurrences.put(s, new Long(((Long)matchesOccurrences.get(s)).longValue()+1));
					if(matches.add(new MatchInst(match)))
					{
						distinctMatchListener.match(matchedNodes);
						//nofDistinctMatches++;
						//for(int i=0;i<nofStates; i++)
							//nodesMatchesDistinct[matchedNodes[i]]++;
					}
				}
				else
				{
					matchedNodes[patternNodes[si+1]] = -1;
					if(parentType[patternNodes[si+1]] == null)
						candidates[si +1] = tgraph.nodes().keySet().iterator();
					else
					{
						switch(parentType[patternNodes[si +1]])
						{
							case IN:
								candidates[si +1] = tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getInAdiacs().iterator();
								break;
							case OUT:
								candidates[si +1] = tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getOutAdiacs().iterator();
								break;
							default:
							{
								Set<Integer> adiac=new TreeSet<Integer>();
								Set<Integer> outAdiac=tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getOutAdiacs();
								Set<Integer> inAdiac=tgraph.nodes().get(matchedNodes[patternNodes[parentState[si+1]]]).getInAdiacs();
								if(outAdiac!=null)
									adiac.addAll(outAdiac);
								if(inAdiac!=null)
									adiac.addAll(inAdiac);
								candidates[si +1] = adiac.iterator();
							}
						} 
					}
					si++;
				}
			}
		}
	}

	private boolean nodeMatch_simple(int si, int tnode, int[] patternNodes)
	{
		try{
		return 	tgraph.nodes().get(tnode).inDegree()>=pgraph.nodes().get(patternNodes[si]).inDegree() && 
				tgraph.nodes().get(tnode).outDegree()>=pgraph.nodes().get(patternNodes[si]).outDegree() &&
				pgraph.compatibleNode(pgraph.nodes().get(patternNodes[si]).getAttribute(), tgraph.nodes().get(tnode).getAttribute());
		}catch(Exception e){
			return false;
		}
	}
}