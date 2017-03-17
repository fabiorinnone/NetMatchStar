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

import java.util.Enumeration;
import java.util.Hashtable;

import netmatch.utils.Common;
import netmatch.utils.MyInteger;

public class ExactEdgeComparator implements AttrComparator {
  public boolean compatible(Object o1, Object o2) {// o1 = query - o2 = graph
    Hashtable h1 = (Hashtable)o1;
    Hashtable h2 = (Hashtable)o2;

    /*System.out.println("compare:");
    printHashTable(h1);
    printHashTable(h2);*/

    Hashtable tmp1 = new Hashtable();
    Hashtable tmp2 = new Hashtable(h2);
    for(Enumeration e1 = h1.keys();e1.hasMoreElements();) {
      String key = (String)e1.nextElement();
      MyInteger i1 = (MyInteger)h1.get(key);
      if(key.startsWith(Common.ANY_LABEL))
        tmp1.put(key, i1);
      else {
        if(h2.containsKey(key)) {
          MyInteger i2 = (MyInteger)h2.get(key);
          if(i1.great(i2))
            return false;
          tmp2.remove(key);
          if(i2.intValue() - i1.intValue() > 0)
            tmp2.put(key, new MyInteger(i2.intValue() - i1.intValue()));
        }
        else
          return false;
      }
    }
    while(!tmp1.isEmpty()) {
      int max = extractMax(tmp1);
      if(!isFeasible(tmp2, max))
        return false;
    }
    return true;
  }

  private int extractMax(Hashtable tmp) {
    int max = 0;
    String key, maxkey = null;
    for(Enumeration e1 = tmp.keys();e1.hasMoreElements();) {
      key = (String)e1.nextElement();
      MyInteger i1 = (MyInteger)tmp.get(key);
      if(max < i1.intValue()) {
        maxkey = key;
        max = i1.intValue();
      }
    }
    tmp.remove(maxkey);
    return max;
  }

  private boolean isFeasible(Hashtable tmp, int val) {
    int min = Integer.MAX_VALUE;
    String key, minkey = null;
    for(Enumeration e1 = tmp.keys();e1.hasMoreElements();) {
      key = (String)e1.nextElement();
      MyInteger i1 = (MyInteger)tmp.get(key);
      if(min >= i1.intValue() && i1.intValue() >= val) {
        minkey = key;
        min = i1.intValue();
      }
    }
    if(minkey != null) {
      tmp.remove(minkey);
      return true;
    }
    return false;
  }

  public void printHashTable(Hashtable h) {
    for(Enumeration e1 = h.keys();e1.hasMoreElements();) {
      String key = (String)e1.nextElement();
      MyInteger i1 = (MyInteger)h.get(key);
      System.out.println(key+" "+i1.toString());
    }
  }
  /*public boolean compatible(Object o1, Object o2) { // o1 = query - o2 = graph
    Hashtable h1 = (Hashtable)o1;
    Hashtable h2 = (Hashtable)o2;
    for(Enumeration e1 = h1.keys();e1.hasMoreElements();) {
      String key = (String)e1.nextElement();
      myInteger i1 = (myInteger)h1.get(key);
      if(h2.containsKey(key)) {
        myInteger i2 = (myInteger)h2.get(key);
        if(i1.great(i2))
          return false;
      }
      else
        return false;
    }
    return true;
  }*/
}