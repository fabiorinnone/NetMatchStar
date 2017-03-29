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
package it.unict.dmi.netmatchstar.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.cytoscape.model.CyNode;

public class Common {
	public static final String APP_NAME = "NetMatch*";

	public static int indexN = 0;
	
	//key is suid of network, value is motif type
	public static HashMap<Long, Integer> motifsMap;
			
	//key is suid of mton fan, value is a pair of lists of nodes of network:
	//first list contains m-nodes, second one contains n-nodes
	public static HashMap<Long, Pair<ArrayList<CyNode>>> mtonFanMap;
	
	public static String NODE_QUERY_ATTR = "QueryNetwork - Nodes Attributes";
	public static String EDGE_QUERY_ATTR = "QueryNetwork - Edges Attributes";
	
	public static String NODE_ID_ATTR = "shared name";
	public static String EDGE_ID_ATTR = "shared name";	
	public static String NODE_NAME_ATTR = "name";
	public static String EDGE_NAME_ATTR = "name";
	public static String EDGE_INTER_ATTR = "shared interaction";
	
	public static String NODE_LABEL_ATTR = "Node Label";
	public static String EDGE_LABEL_ATTR = "Edge Label";
	
	public static String NETMATCH_STYLE = Common.APP_NAME + "Style";
	
	//motifs types
	public static final Integer THREE_CHAIN = 1;
	public static final Integer FEED_FORWARD_LOOP = 2;
	public static final Integer BI_PARALLEL = 3;
	public static final Integer BI_FAN = 4;
	public static final Integer MTON_FAN = 5;

	public static final String SELF_EDGE = "[SELFEDGE]";
	public static final String STD_EDGE = "[STDEDGE]";
	public static final String ANY_LABEL = "?";
	public static final int NULL_NODE = 0xFFFF;
	public static boolean DIRECTED = true;
	public static boolean LABELED = true;
	public static int imageSize = 80;

	public static final String APPROX_GT = "?>";
	public static final String APPROX_GE = "?>=";
	public static final String APPROX_EQ = "?=";
	public static final String APPROX_LT = "?<";
	public static final String APPROX_LE = "?<=";

	public static final String GT = ">";
	public static final String GE = ">=";
	public static final String EQ = "=";
	public static final String LT = "<";
	public static final String LE = "<=";
	public static final String UNDEFINED = "UNDEFINED";

	public static boolean isApproximatePath(String s) {
		return 	s.startsWith(APPROX_GT) ||  s.startsWith(APPROX_GE) || 
				s.startsWith(APPROX_EQ) ||  s.startsWith(APPROX_LT) || 
				s.startsWith(APPROX_LE);
	}

	public static String isNumber(String s) {
		if(s.startsWith(GE))
			return GE;
		else if(s.startsWith(GT))
			return GT;
		else if(s.startsWith(LE))
			return LE;
		else if(s.startsWith(LT))
			return LT;
		return UNDEFINED;
	}

	public static String getNumber(String s) {
		int index = s.indexOf("=");
		if(s.charAt(1) == '=')
			return s.substring(2);
		else
			return s.substring(1);
	}
}
