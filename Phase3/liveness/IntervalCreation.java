package liveness;

import cs132.vapor.ast.*;
import java.util.*;
import registeralloc.*;

public class IntervalCreation {
    private List<Set<String>> inSet = new ArrayList<>();
    private List<Set<String>> outSet = new ArrayList<>();
    private Map<Integer,Boolean> loops = new HashMap<>();

    public List<Interval> createIntervals(VFunction vfunction) {
        Graph currentfunc = new Graph(); // Control flow graph for the current function.

        //Create label mappings in our graph, for referencing labels.
        for(int j = 0; j < vfunction.labels.length; j++) {
            currentfunc.labelLines.put(vfunction.labels[j].ident, vfunction.labels[j].instrIndex);
        }

        // Create control flow graph
        for(int j = 0; j < vfunction.body.length; j++) {
            vfunction.body[j].accept(vfunction, currentfunc);
        }
        //currentfunc.printGraph();
        
        // Begin running in/out set creation.
        List<Set<String>> inSetPrime = new ArrayList<>();
        List<Set<String>> outSetPrime = new ArrayList<>();
        for(int i = 0; i < vfunction.body.length; i++) {
            inSet.add(new HashSet<>());
            inSetPrime.add(new HashSet<>());
            outSet.add(new HashSet<>());
            outSetPrime.add(new HashSet<>());
        }
        int itval = 1;
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
            /*
            System.out.println("Iteration"  + itval++); // DEBUG, shows iterations.
            for(int i = 0; i < vfunction.body.length; i++) {
                System.out.println("Line " + i + " In: " + inSet.get(i) + " Out: " + outSet.get(i));
            }
            System.out.println();
            */
        } while(equalInOutSets(inSetPrime, outSetPrime));

        /*
        for(int i = 0; i < inSet.size(); i++) { // DEBUG TEXT LOOP
            System.out.println("Line " + i + ": In: " + inSet.get(i) + " Out: " + outSet.get(i));
        }
        */
        
        detectLoops(currentfunc); // fills out loops. True for a line in a loop, false otherwise.

        List<Interval> totals = new ArrayList<>();
        Map<String,Interval> currIntervals = new HashMap<>();

        // Handle first line in set independently.
        for(String s : inSet.get(0)) {
            Interval tmpI = new Interval(s, 0);
            currIntervals.put(s, tmpI);
            if(!outSet.get(0).contains(s)) {
                tmpI.setEnd(0);
                totals.add(tmpI);
                currIntervals.remove(s);
            }
        }

        for(int i = 0; i < vfunction.body.length-1; i++) { // Handle EVERYTHING but return line
            Set<String> nextIn = inSet.get(i+1);
            Set<String> currOut = outSet.get(i);
            for(String outval : currOut) { // for all items in the out set
                if(!currIntervals.containsKey(outval)) { // If we already have an interval for it, dont need another yet.
                    Interval tmpI = new Interval(outval, i);
                    currIntervals.put(outval, tmpI);
                }
            }

            // TODO: Might want to make it so values created in a loop can die in a loop, using way too much stack space.
            if(!loops.get(i)) { // not in a loop, remove values normally. 
                Set<String> tmpSet = new HashSet<>(currIntervals.keySet());
                for(String s : tmpSet) {
                    if(!nextIn.contains(s)) { // If not in the next in set, died this line.
                        Interval tmpI = currIntervals.remove(s);
                        tmpI.setEnd(i);
                        totals.add(tmpI);
                    }
                }
            }
        }

        for(String s : inSet.get(vfunction.body.length-1)) { // Literally should only be one thing, the return value if one exists, else this wont run.
            Interval tmpI = currIntervals.remove(s);
            tmpI.setEnd(vfunction.body.length-1);
            totals.add(tmpI);
        }

        for(Interval s: currIntervals.values()) { // Remove remaining intervals, assign death to second to last line. No need to remove from currIntervals map.
            s.setEnd(vfunction.body.length-2);
            totals.add(s);
        }
        

        return totals;
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

    private void detectLoops(Graph g) {
        for(int i = 0; i < g.graph.size(); i++) {
            List<Integer> adj = g.graph.get(i);
            loops.put(i, false);
            for(int j = 0; j < adj.size(); j++) {
                if(adj.get(j) < i) {
                    for(int k = adj.get(j); k <= i; k++) {
                        loops.put(k, true);
                    }
                }
            }
        }
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