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

import netmatch.utils.Common;

public class ExactNodeComparator implements AttrComparator {
  public ExactNodeComparator() {
  }

  public boolean compatible(Object p1, Object p2) throws Exception {
    ArrayList t;
    String q;
    boolean isNumber = false;
    double number = 0;
    String prefix;
    //System.out.println("Classe di p1 "+p1.getClass() );
    //System.out.println("Classe di p2 "+p2.getClass() );


    if(p1 instanceof ArrayList) {
      t = (ArrayList)p1;
      q = (String)p2;
    }
    else {
      t = (ArrayList)p2;
      q = (String)p1;
    }
    if(((String)t.get(0)).endsWith(Common.SELF_EDGE) == q.endsWith(Common.SELF_EDGE)) {
      if(q.startsWith(Common.ANY_LABEL))
        return true;
      if(q.endsWith(Common.SELF_EDGE))
        q = q.substring(0,q.length()-Common.SELF_EDGE.length());
      if(!(prefix = Common.isNumber(q)).equals(Common.UNDEFINED)) {
        try {
          number = Double.parseDouble(Common.getNumber(q));
          isNumber = true;
        }
        catch(NumberFormatException e) {
          isNumber = false;
        }
      }
      for(int i = 1; i < t.size();i++) {
        Object obj = t.get(i);
        if(obj.toString().startsWith(Common.ANY_LABEL))
          return true;
        if(isNumber) {
          try {
            double number2 = Double.parseDouble(obj.toString());
            if(prefix.equals(Common.GT))
              return number2 > number;
            else if(prefix.equals(Common.GE))
              return number2 >= number;
            else if(prefix.equals(Common.LT))
              return number2 < number;
            return number2 <= number;
          }
          catch(NumberFormatException e) {
            // Nothing
          }
        }
        if(obj.toString().equals(q))
          return true;
      }
    }
    return false;
    /*String pa = p1.toString();
    String pb = p2.toString();

    if((pa.endsWith(Common.SELF_EDGE) && !pb.endsWith(Common.SELF_EDGE)) || (!pa.endsWith(Common.SELF_EDGE) && pb.endsWith(Common.SELF_EDGE)))
      return false;
    if(pa.startsWith(Common.ANY_LAzBEL) || pb.startsWith(Common.ANY_LABEL) || pa.equals(pb))
      return true;
    return false;*/
  }
}