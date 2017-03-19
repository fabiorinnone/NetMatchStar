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
package it.unict.dmi.netmatchstar.algorithm;

//import cytoscape.CyNetwork;
//import cytoscape.CyEdge;

import java.util.ArrayList;

import it.unict.dmi.netmatchstar.graph.EdgePair;
import it.unict.dmi.netmatchstar.graph.Graph;
import it.unict.dmi.netmatchstar.utils.Common;

public class BfsPath extends ArrayList {
	
	public BfsPath() {
		
	}

	public BfsPath(BfsPath path) {
		addAll(path);
	}

	public void add(Graph.Edge edge, boolean isOutGoingEdge) {
		super.add(new EdgePair(edge, isOutGoingEdge));
	}

	public boolean contains(Graph.Edge edg) {
		for(int i = 0; i<size();i++) {
			EdgePair p = (EdgePair)get(i);
			if(p.getEdge() == edg)
				return true;
		}
		return false;
	}

	public EdgePair getLastEdge() {
		return (EdgePair)get(size()-1);
	}

	public boolean checkCondition(String cond, int number) {
		int level = size();
		if(cond.equals(Common.GT))
			return level > number;
		else if(cond.equals(Common.GE))
			return level >= number;
		else if(cond.equals(Common.EQ))
			return level == number;
		else if(cond.equals(Common.LT))
			return level < number;
		else if(cond.equals(Common.LE))
			return level <= number;
		return false;
	}

	public Graph.Edge[] getApproximatePath() {
		int size = size();
		Graph.Edge path[] = new Graph.Edge[size];
		for(int i = 0; i < size; i++) {
			EdgePair p = (EdgePair)get(i);
			path[i] = p.getEdge(); //((Integer)get(i)).intValue();
		}
		return path;
	}
}