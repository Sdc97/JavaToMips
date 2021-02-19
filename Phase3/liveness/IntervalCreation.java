package liveness;

import cs132.vapor.ast.*;
import java.util.*;
import registeralloc.*;

public class IntervalCreation {
    List<Set<String>> inSet = new ArrayList<>();
    List<Set<String>> outSet = new ArrayList<>();

    public List<Interval> createIntervals(VFunction vfunction) {
        System.out.println(vfunction.ident + " " + vfunction.index);
        Graph currentfunc = new Graph(); // Control flow graph for the current function.

        for(int j = 0; j < vfunction.labels.length; j++) {
            currentfunc.labelLines.put(vfunction.labels[j].ident, vfunction.labels[j].instrIndex);
        }

        for(int j = 0; j < vfunction.body.length; j++) {
            vfunction.body[j].accept(vfunction, currentfunc);
        }
        currentfunc.printGraph();
        
        List<Set<String>> inSetPrime = new ArrayList<>();
        List<Set<String>> outSetPrime = new ArrayList<>();
        for(int i = 0; i < vfunction.body.length; i++) {
            inSet.add(new HashSet<>());
            inSetPrime.add(new HashSet<>());
            outSet.add(new HashSet<>());
            outSetPrime.add(new HashSet<>());
        }

        do {
            for(int i = 0; i < vfunction.body.length; i++) {
                inSetPrime.set(i, new HashSet<>(inSet.get(i))); // set prime in set
                outSetPrime.set(i, new HashSet<>(outSet.get(i))); // set prime out set
                Set<String> currUse = currentfunc.useSet.get(i); // retrieve current use set.
                Set<String> newInSet = new HashSet<>(outSet.get(i)); // Create and copy new in set from outSet
                newInSet.remove(currentfunc.defSet.get(i)); // remove the def element, if it exists
                newInSet.addAll(currUse); // union with use set
                inSet.set(i, newInSet); // in set equal to union of use set and difference of out - def
                List<Integer> succ = currentfunc.graph.get(i); // Successor set (Either one or two nodes)
                for(int j = 0; j < succ.size(); j++) {
                    outSet.get(i).addAll(inSet.get(succ.get(j))); // Union across all successor in sets.
                }
            }
        } while(equalInOutSets(inSetPrime, outSetPrime));

        for(int i = 0; i < inSet.size(); i++) {
            System.out.println("Line " + i + ": In: " + inSet.get(i) + " Out: " + outSet.get(i));
        }

        Map<String,Interval> currIntervals = new HashMap<>();
        for(int i = 0; i < vfunction.body.length; i++) {
            Set<String> currIn = inSet.get(i);
            Set<String> currOut = outSet.get(i);

            for(String tempval : currIn) {
            }
        }

        return null; // temp
    }

    private boolean equalInOutSets(List<Set<String>> inPrime, List<Set<String>> outPrime) {

        for(int i = 0; i < inSet.size(); i++) {
            if(!inSet.get(i).equals(inPrime.get(i))) {
                return true;
            }
        }

        for(int i = 0; i < outSet.size(); i++) {
            if(!outSet.get(i).equals(outPrime.get(i))) {
                return true;
            }
        }

        return false;
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