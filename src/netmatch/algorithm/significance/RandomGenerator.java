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
package netmatch.algorithm.significance;

import netmatch.graph.Graph;

import java.util.*;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class RandomGenerator
{
	private HashMap<Integer,Graph.Node> targetNodes;
	//private Vector<Graph.Edge> edges;
	private int numNodes;
	private int numEdges;
	private boolean directed;
	private long seed;
	private boolean customSeed;
	
	//Creates a generator of random directed or undirected graphs with a fixed number of nodes and edges
	public RandomGenerator(HashMap<Integer,Graph.Node> targetNodes, int numNodes, 
			int numEdges, boolean directed)
	{
		this.targetNodes = targetNodes;
		this.numNodes = numNodes;
		this.numEdges = numEdges;
		this.directed = directed;
		this.customSeed = false;
	}
	
	public RandomGenerator(HashMap<Integer,Graph.Node> targetNodes, int numNodes,
			int numEdges, boolean directed, long seed) {
		this(targetNodes, numNodes, numEdges, directed);
		this.seed = seed;
		this.customSeed = true;
	}
	
	public Graph createErdosRenyi()
	{
		Graph randomNet = new Graph(directed);
		//Add nodes
		int i = 0;
		for(i = 0; i < numNodes; i++) {
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
		}
		//Select two nodes at random and connect them by an edge
		Random random;
		if (!customSeed)
			random = new Random();
		else 
			random = new Random(seed);
		
		i = 0;
		while(i < numEdges) 
		{
			//Graph.Edge edge = edges.get(i);
			//Object attr = edge.getAttribute();
			int source = random.nextInt(numNodes);
			int target = random.nextInt(numNodes);
			if(!randomNet.isEdge(source,target) && source != target)
			{
				randomNet.addEdge(source, target);
				i++;
			}
		}
		return randomNet;
	}
	
	//Beta: rewiring parameter
	public Graph createWattsStrogatz(double beta)
	{
		//Estimate the average degree that nodes must have in the random network 
		int degree=numEdges/numNodes+1;
		Graph randomNet=new Graph(directed);
		//Add nodes
		int i=0;
		for(i=0;i<numNodes;i++) {
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
		}
		//Make sure that the degree chosen is feasbile
		if(2*degree>(numNodes-1)/2)
			degree=(numNodes-1)/2;
		//Create lattice of numNodes e avgDegree
		for(i=0;i<numNodes;i++) 
		{
			int start=i-degree;
			if(start<0)
				start=numNodes+start;
			int count=0;
			int stop=2*degree;
			while(count<stop)
			{
				if(i!=start)
				{
					if(i<=start)
						randomNet.addEdge(i,start);
					if(i!=start)
						count++;
				}
				start=(start+1)%numNodes;
			}
		}
		//Remove random edges from lattice to get a network with the same number of edges
		Random r;
		if (!customSeed)
			r = new Random();
		else
			r = new Random(seed);
		int currentNumEdges=randomNet.getEdgeCount();
		HashMap<Integer,Graph.Node> nodi=randomNet.nodes();
		//for(i=currentNumEdges;i>numEdges;i--)
		i=currentNumEdges;
		while(i>numEdges)
		{
			HashSet<Integer> adiacs = null;
				int source=r.nextInt(numNodes);
				adiacs=nodi.get(source).getOutAdiacs();
				if (adiacs.size() > 0) {
					Integer[] listAdiac=adiacs.toArray(new Integer[adiacs.size()]);
					int dest=listAdiac[r.nextInt(listAdiac.length)];
					randomNet.removeEdge(source,dest);
					i--;
				}
		}
		//Shuffling each edge with probability beta
		Iterator<Integer> it=nodi.keySet().iterator();
		while(it.hasNext())
		{
			int source=it.next();
			HashSet<Integer> adiacs=nodi.get(source).getOutAdiacs();
			Integer[] listAdiac=adiacs.toArray(new Integer[adiacs.size()]);
			for(i=0;i<listAdiac.length;i++)
			{
				if(r.nextDouble()<=beta)
				{
					boolean found=false;
					while(!found)
					{
						int k=r.nextInt(numNodes);
						if(!adiacs.contains(k) && k!=source)
						{
							randomNet.removeEdge(source,listAdiac[i]);
							randomNet.addEdge(source,k);
							found=true;
						}
					}
				}
			}
		}
		return randomNet;
	}
	
	//initNumNodes: number of node in the initial seed network
	public Graph createAlbertBarabasi(int initNumNodes)
	{
		if (initNumNodes > numNodes) {
			initNumNodes = numNodes;
		}
		//Estimate parameters
		int initEdges=0;
		if(directed)
			initEdges=initNumNodes*(initNumNodes-1);
		else
			initEdges=initNumNodes*(initNumNodes-1)/2;
		int edgesPerNode;
		if (numNodes-initNumNodes==0)
			edgesPerNode=1;
		else
			edgesPerNode=(numEdges-initEdges)/(numNodes-initNumNodes);
		Graph randomNet=new Graph(directed);
		
		//Add nodes
		int currentNumEdges=0;
		int i=0, j=0, m=0;
		for(i=0;i<numNodes;i++) {
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
		}
		
		//Set up the initial complete seed network
		for(i=0;i<initNumNodes;i++) 
		{
			for(j=0;j<initNumNodes;j++) 
			{
				if(j!=i && (directed || j>i))
				{
					randomNet.addEdge(i,j);
					currentNumEdges++;
				}
			}
		}

		//Add each node one at a time and connect it to edgesPerNode nodes with probability proportional to the degree of existing nodes
		Random random;
		if (!customSeed)
			random = new Random();
		else 
			random = new Random(seed);
		for(i=initNumNodes;i<numNodes;i++) 
		{
			int added = 0;
			double degreeIgnore = 0;
			//Add the appropriate number of edges
			for(m=0;m<edgesPerNode;m++) 
			{
				double prob = 0;
				double randNum = random.nextDouble();
				//Try to add this node to every existing node
				for(j=0;j<i;j++) 
				{
					if(!randomNet.isEdge(i,j))
					{
						int degreeJ=randomNet.nodes().get(j).outDegree();
						prob+=((double)degreeJ)/((double)(2*numEdges)-degreeIgnore);
					}
					if (randNum<=prob) 
					{
						// Create and edge between node i and node j
						randomNet.addEdge(i,j);
						degreeIgnore+=randomNet.nodes().get(j).outDegree();
						added++;
						//Stop iterating for this probability, once we have found a single edge
						break;
					}
				}
			}
			currentNumEdges+=added;
		}
		//Complete the network
		while(currentNumEdges<numEdges)
		{
			i=random.nextInt(numNodes-initNumNodes)+initNumNodes;
			double degreeIgnore = 0;
			double prob = 0;
			double randNum = random.nextDouble();
			//Try to add this node to every existing node
			for(j=0;j<i;j++) 
			{
				if(!randomNet.isEdge(i,j))
				{
					int degreeJ=randomNet.nodes().get(j).outDegree();
					prob+=((double)degreeJ)/((double)(2*numEdges)-degreeIgnore);
				}
				if(randNum<=prob) 
				{
					// Create and edge between node i and node j
					randomNet.addEdge(i,j);
					degreeIgnore+=randomNet.nodes().get(j).outDegree();
					currentNumEdges++;
					//Stop iterating for this probability, once we have found a single edge
					break;
				}
			}
		}
		return randomNet;
	}
	
	//dim: the space dimension of points in the random model 
	public Graph createGeometric(int dim)
	{
		Hashtable<Integer,float[]> mapCoord=new Hashtable<Integer,float[]>();
		Random random;
		if (!customSeed)
			random = new Random();
		else
			random = new Random(seed);
		Graph randomNet=new Graph(directed);
		//Add nodes and associate random coordinates to nodes in the unit space (or plane in two dimensions)
		int i=0, j=0, k=0;
		for(i=0;i<numNodes;i++)
		{
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
			float[] coord=new float[dim];
			for(j=0;j<dim;j++)
				coord[j]=random.nextFloat();
			mapCoord.put(i,coord);
		}
		//Compute distances between all pairs on nodes
		float[] values=new float[numNodes*(numNodes-1)/2];
		int l=0;
		for(i=0;i<numNodes;i++)
		{
			float[] coordSource=mapCoord.get(i);
			for(j=i+1;j<numNodes;j++)
			{
				float[] coordDest=mapCoord.get(j);
				float dist=0f;
				for(k=0;k<dim;k++)
					dist+=Math.pow(coordSource[k]-coordDest[k],2);
				dist=(float)Math.sqrt(dist);
				values[l]=dist;
				l++;
			}
		}
		//Sort distances to estimate the distance threshold for edges in the random network
		Arrays.sort(values);
		float maxDist=values[numEdges-1];
		//Add edges for each pair of nodes at distance at most maxDist (between 0 and 1)
		for(i=0;i<numNodes;i++)
		{
			float[] coordSource=mapCoord.get(i);
			for(j=i+1;j<numNodes;j++)
			{
				float[] coordDest=mapCoord.get(j);
				float dist=0f;
				for(k=0;k<dim;k++)
					dist+=Math.pow(coordSource[k]-coordDest[k],2);
				dist=(float)Math.sqrt(dist);
				if(dist<=maxDist)
					randomNet.addEdge(i,j);
			}
		}
		return randomNet;
	}
	
	//numAmb: number of ambassadors for incoming nodes
	public Graph createForestFire(int numAmb)
	{
		//Estimate parameter pForward for the forest fire model
		double a=0, b=0, c=0;
		if(directed)
		{
			a=1.723143*Math.pow(numNodes,0.05480804);
			b=2.433498*Math.pow(numNodes,-0.5349726);
			c=-2.655774*Math.pow(numNodes,0.1168572);
		}
		else
		{
			a=2.419132*Math.pow(numNodes,0.0067785);
			b=0.01406629*Math.pow(numNodes,-0.1960128);
			c=-11.2962*Math.pow(numNodes,0.02540518);
		}
		double ratio=(double)numEdges/(double)numNodes;
		//Adjust parameters if necessary to avoid negative probabilities or probabilities>1
		if(ratio-a<0)
			ratio=a+0.025;
		double pForward=Math.log((ratio-a)/b)/-c;
		if(pForward>1)
			pForward=1;
		if(pForward<0)
			pForward=0.0001;
		
		//Create an initial complete network with numAmb ambassadors
		int i=0, j=0;
		Random random;
		if (!customSeed)
			random = new Random();
		else 
			random = new Random(seed);
		Graph randomNet=new Graph(directed);
		int numRandomEdges=0;
		for(i=0;i<numAmb;i++) {
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
		}
		for(i=0;i<numAmb;i++)
		{
			for(j=i+1;j<numAmb;j++)
			{
				randomNet.addEdge(i,j);
				if(directed)
					randomNet.addEdge(j,i);
				numRandomEdges++;
			}
		}
		
		//Run forest fire on remaining nodes
		for(i=numAmb;i<numNodes;i++)
		{
			HashSet<Integer> amb=new HashSet<Integer>();
			Vector<Integer> queue=new Vector<Integer>();
			HashSet<Integer> visited=new HashSet<Integer>();
			//Choose numAmb ambassadors and connect node i to them
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
			visited.add(i);
			while(amb.size()<numAmb)
			{
				int selAmb=random.nextInt(i);
				if(amb.add(selAmb))
				{
					if(!randomNet.isEdge(i,selAmb))
					{
						randomNet.addEdge(i,selAmb);
						numRandomEdges++;
					}
					queue.add(selAmb);
					visited.add(selAmb);
				}
			}
			//Explore the forest of the ambassadors and "burn" some edges until possible
			while(!queue.isEmpty())
			{
				int start=queue.remove(0);
				HashSet<Integer> adiac=randomNet.nodes().get(start).getOutAdiacs();
				if(!adiac.isEmpty())
				{
					//Select the number of out-links to burn
					int numCand=1;
					double prob=1;
					while(true)
					{
						prob=prob*pForward;
						double rand=random.nextDouble();
						if(rand<=prob)
							numCand++;
						else
						{
							numCand--;
							break;
						}
					}
					//Select numCand out-links of the ambassador and connect node i to them
					Iterator<Integer> it=adiac.iterator();
					j=0;
					while(it.hasNext() && j<numCand)
					{
						int idAdiac=it.next();
						if(!visited.contains(idAdiac))
						{
							if(!randomNet.isEdge(i,idAdiac))
							{
								randomNet.addEdge(i,idAdiac);
								numRandomEdges++;
							}
							queue.add(idAdiac);
							visited.add(idAdiac);
							j++;
						}
					}
				}
			}
		}
		//If random network has less edges, complete it by adding new edges with forest fire models starting from random existing nodes
		while(numRandomEdges<numEdges)
		{
			i=random.nextInt(numNodes-1)+1;
			//System.out.println(i);
			HashSet<Integer> amb=new HashSet<Integer>();
			Vector<Integer> queue=new Vector<Integer>();
			HashSet<Integer> visited=new HashSet<Integer>();
			//Choose numAmb ambassadors and connect node i to them
			visited.add(i);
			while(amb.size()<numAmb)
			{
				int selAmb=random.nextInt(randomNet.getNodeCount());
				if(selAmb!=i && amb.add(selAmb))
				{
					if(!randomNet.isEdge(i,selAmb))
					{
						randomNet.addEdge(i,selAmb);
						numRandomEdges++;
					}
					queue.add(selAmb);
					visited.add(selAmb);
				}
				if(numRandomEdges==numEdges)
					break;
			}
			if(numRandomEdges==numEdges)
				break;
			//Explore the forest of the ambassadors and "burn" some edges until possible
			while(!queue.isEmpty())
			{
				int start=queue.remove(0);
				HashSet<Integer> adiac=randomNet.nodes().get(start).getOutAdiacs();
				if(!adiac.isEmpty())
				{
					//Select the number of out-links to burn
					int numCand=1;
					double prob=1;
					while(true)
					{
						prob=prob*pForward;
						double rand=random.nextDouble();
						if(rand<=prob)
							numCand++;
						else
						{
							numCand--;
							break;
						}
					}
					//Select numCand out-links of the ambassador and connect node i to them
					Iterator<Integer> it=adiac.iterator();
					j=0;
					while(it.hasNext() && j<numCand)
					{
						int idAdiac=it.next();
						if(!visited.contains(idAdiac))
						{
							if(!randomNet.isEdge(i,idAdiac))
							{
								randomNet.addEdge(i,idAdiac);
								numRandomEdges++;
							}
							queue.add(idAdiac);
							visited.add(idAdiac);
							j++;
						}
						if(numRandomEdges==numEdges)
							break;
					}
				}
				if(numRandomEdges==numEdges)
					break;
			}
		}
		//If random network has more edges, remove them randomly until the random network has the same number of edges of the input network
		while(numRandomEdges>numEdges)
		{
			i=random.nextInt(randomNet.getNodeCount());
			HashSet<Integer> adiacs=randomNet.nodes().get(i).getOutAdiacs();
			Integer[] listAdiac=adiacs.toArray(new Integer[adiacs.size()]);
			if(listAdiac.length>0)
			{
				int idAdiac=listAdiac[random.nextInt(listAdiac.length)];
				randomNet.removeEdge(i,idAdiac);
				numRandomEdges--;
			}
		}
		return randomNet;
	}
	
	//initNumNodes: number of nodes of the initial seed network
	//initProbEdges: probability to add an edge between two nodes in the seed network
	public Graph createDuplication(int initNumNodes, double initProbEdges)
	{
		if (initNumNodes > numNodes) {
			initNumNodes = numNodes;
		}
		//Estimate parameter probDupl for the duplication model
		double a=0, b=0, c=0;
		if(directed)
		{
			a=1.658038*Math.pow(numNodes,-0.04522255);
			b=126.3442*Math.pow(numNodes,-1.096679);
			c=-1.530963*Math.pow(numNodes,0.1712619);
		}
		else
		{
			a=0.7622774*Math.pow(numNodes,0.102159);
			b=13.11378*Math.pow(numNodes,-0.7131595);
			c=-2.63873*Math.pow(numNodes,0.1613185);
		}
		//Adjust parameters if necessary to avoid negative probabilities or probabilities>1
		double ratio=(double)numEdges/(double)numNodes;
		if(ratio-a<0)
			ratio=a+0.0085;
		double probDupl=Math.log((ratio-a)/b)/-c;
		//System.out.println(probDupl);
		if(probDupl>1)
			probDupl=1;
		if(probDupl<0)
			probDupl=0.0001;
		
		//Create an initial random graph with Erdos-Renyi model
		int initNumEdges=0;
		if(directed)
			initNumEdges=(int)((initNumNodes*(initNumNodes-1))*initProbEdges);
		else
			initNumEdges=(int)(((initNumNodes*(initNumNodes-1))/2)*initProbEdges);
		Graph randomNet=new Graph(directed);
		int i=0;
		for(i=0;i<initNumNodes;i++) {
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
		}
		Random random;
		if (!customSeed)
			random = new Random();
		else
			random = new Random(seed);
		i=0;
		while(i<initNumEdges) 
		{
			int source=random.nextInt(initNumNodes);
			int target=random.nextInt(initNumNodes);
			if(!randomNet.isEdge(source,target) && source!=target)
			{
				randomNet.addEdge(source,target);
				i++;
			}
		}
		
		//Run duplication model
		i=randomNet.nofNodes();
		while(i<numNodes)
		{
			//Select a random node u in the graph
			int u=random.nextInt(randomNet.nofNodes());
			//Add new node i to the graph
			Graph.Node node = targetNodes.get(i);
			Object attr = node.getAttribute();
			randomNet.addNode(i, attr);
			//Connect i to neighbors of u with probability p for each edge
			HashSet<Integer> adiacs=randomNet.nodes().get(u).getOutAdiacs();
			if(adiacs.size()>0)
			{
				Iterator<Integer> it=adiacs.iterator();
				boolean connected=false;
				while(it.hasNext())
				{
					int idAdiac=it.next();
					if(random.nextDouble()<=probDupl)
					{
						randomNet.addEdge(i,idAdiac);
						connected=true;
					}
				}
				if(connected)
					i++;
			}
		}
		
		int numCurrentEdges=randomNet.nofEdges();
		if(probDupl==1.0)
		{
			//Complete network with random edges between nodes
			while(numCurrentEdges<numEdges)
			{
				int source=random.nextInt(randomNet.nofNodes());
				int target=random.nextInt(randomNet.nofNodes());
				if(!randomNet.isEdge(source,target) && source!=target)
				{
					randomNet.addEdge(source,target);
					numCurrentEdges++;
				}
			}
		}
		else
		{
			while(numCurrentEdges<numEdges)
			{
				/*//Add other edges with duplication model, in order to build a network with the same number of edges of the input network
				i=random.nextInt(randomNet.nofNodes());
				int u=random.nextInt(randomNet.nofNodes());
				if(i!=u)
				{
					HashSet<Integer> adiacs=randomNet.nodes().get(u).getOutAdiacs();
					Integer[] listAdiac=adiacs.toArray(new Integer[adiacs.size()]);
					if(listAdiac.length>0) {
						int idAdiac=listAdiac[random.nextInt(listAdiac.length)];
						if(idAdiac!=i && !randomNet.isEdge(i,idAdiac) && random.nextDouble()<=probDupl)
						{
							randomNet.addEdge(i,idAdiac);
							numCurrentEdges++;
						}
					}
				}*/
				int source=random.nextInt(randomNet.nofNodes());
				int target=random.nextInt(randomNet.nofNodes());
				if(!randomNet.isEdge(source,target) && source!=target)
				{
					randomNet.addEdge(source,target);
					numCurrentEdges++;
				}
			}
			while(numCurrentEdges>numEdges)
			{
				//Remove random edges, in order to build a network with the same number of edges of the input network
				i=random.nextInt(randomNet.nofNodes());
				HashSet<Integer> adiacs=randomNet.nodes().get(i).getOutAdiacs();
				Integer[] listAdiac=adiacs.toArray(new Integer[adiacs.size()]);
				if(listAdiac.length>0)
				{
					int idAdiac=listAdiac[random.nextInt(listAdiac.length)];
					randomNet.removeEdge(i,idAdiac);
					numCurrentEdges--;
				}
			}
		}
		return randomNet;
	}
}