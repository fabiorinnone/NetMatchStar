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
package it.unict.dmi.netmatchstar.algorithm.metrics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import it.unict.dmi.netmatchstar.graph.Graph;
import it.unict.dmi.netmatchstar.graph.Graph.Node;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class Metrics
{
	public static double getAverageDegree(Graph g) {
		HashMap<Integer,Graph.Node> nodes = g.nodes();
		int numNodes = g.getNodeCount();
		double sum = 0.0;
		for (int i = 0; i < numNodes; i++) {
			Node node = nodes.get(i);
			sum += node.outDegree();
		}
		return sum / numNodes;
	}
	
	public static double getAverageClusteringCoefficient(Graph g) {
		HashMap<Integer,Graph.Node> nodes = g.nodes();
		int numNodes = g.getNodeCount();
		double sum = 0.0;
		for (int i = 0; i < numNodes; i++) {
			Node node = nodes.get(i);
			sum += getLocalClusteringCoefficient(g, node);
		}
		return sum / numNodes;
	}
	
	private static double getLocalClusteringCoefficient(Graph g, Graph.Node i) {
		HashSet<Integer> adiac = i.getOutAdiacs();
		Integer[] adiacArr = new Integer[adiac.size()];
		adiacArr = adiac.toArray(adiacArr);
		int e_i = 0;
		for (int j = 0; j < adiac.size(); j++) {
			for (int k = 0; k < j; k++) {
				if (g.isEdge(adiacArr[j], adiacArr[k]))
					e_i++;
			}
		}
		
		int k_i = i.outDegree();
		if (k_i <= 1)
			return 1.0;
		else 
			return (2 * e_i)/(k_i * (k_i - 1));
	}
	
	public static double getAssortativity(Graph g)
	{
		HashMap<Integer,Graph.Node> mapNodes=g.nodes();
		Iterator<Integer> it=mapNodes.keySet().iterator();
		double num1=0.0, num2=0.0, den1=0.0;
		double numEdges=0;
		while(it.hasNext())
		{
			int idSource=it.next();
			HashSet<Integer> setAdiacs=mapNodes.get(idSource).getOutAdiacs();
			int sourceDegree=setAdiacs.size();
			numEdges+=sourceDegree;
			Iterator<Integer> it2=setAdiacs.iterator();
			while(it2.hasNext())
			{
				int idDest=it2.next();
				int destDegree=mapNodes.get(idDest).getOutAdiacs().size();
				num1+=sourceDegree*destDegree;
				num2+=0.5*(sourceDegree+destDegree);
				den1+=0.5*(sourceDegree*sourceDegree+destDegree*destDegree);
			}
		}
		num1=num1/numEdges;
		den1=den1/numEdges;
		num2=(num2/numEdges)*(num2/numEdges);
		return (num1-num2)/(den1-num2);
	}
}