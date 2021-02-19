package liveness;

import cs132.vapor.ast.*;
import java.util.*;
import registeralloc.*;

public class IntervalCreation {
    public List<Interval> createIntervals(VaporProgram n) {
        for(int i = 0; i < n.functions.length; i++) {
            VFunction vfunction = n.functions[i];
            System.out.println(vfunction.ident + " " + vfunction.index);
            Graph currentfunc = new Graph();
  
            for(int j = 0; j < vfunction.labels.length; j++) {
              currentfunc.labelLines.put(vfunction.labels[j].ident, vfunction.labels[j].instrIndex);
            }
  
            for(int j = 0; j < vfunction.body.length; j++) {
              vfunction.body[j].accept(vfunction, currentfunc);
            }
            currentfunc.printGraph();
          }


        return null; // temp
    }
}
// $t0 assumed to be taken by c
/*  $t1 = 0
    $t1 = Add($t1 1) // a released t1
    $t0 = Add($t0 $t1)
    $t1 = Mult($t1 2) // a must get t1 back here
    if $t1 < N goto L1
    $v0 = $t0
    ret
*/