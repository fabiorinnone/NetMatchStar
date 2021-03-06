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

import it.unict.dmi.netmatchstar.utils.MyInteger;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;

public class Edge {
  int from;
  int to;
  int pos;
  Hashtable attr;
  Edge next;
  boolean isSelfEdge;

  public Edge(int from, int to, Object name, boolean isSelfEdge) {
    this.from = from;
    this.to = to;
    this.pos = -1;
    this.next = null;
    this.attr = new Hashtable();
    update(name);
    //this.attr.put(name, new myInteger(1));
    this.isSelfEdge = isSelfEdge;
  }

  public void update1(String name) {
    if(attr.containsKey(name)) {
      MyInteger i = (MyInteger)attr.get(name);
      i.setValue(i.intValue() + 1);
    }
    else
      attr.put(name, new MyInteger(1));
  }

  public void update(Object t) {
    if(t instanceof ArrayList) {
      Iterator iterator = ((ArrayList)t).iterator();
      while(iterator.hasNext()) {
        update1(iterator.next().toString());
      }
    }
    else
      update1(t.toString());
  }
}
