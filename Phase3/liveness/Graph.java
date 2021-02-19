package liveness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cs132.vapor.ast.*;

public class Graph extends VInstr.VisitorP<VFunction,RuntimeException> {

    public List<List<Integer>> graph = new Vector<>();
    public Map<Integer,String> defSet = new HashMap<>(); // can only define one variable per line really
    public Map<Integer,List<String>> useSet = new HashMap<>();
    public Map<String,Integer> labelLines = new HashMap<>();
    private int lineno = 0;

    @Override
    public void visit(VFunction arg0, VAssign arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        next.add(lineno + 1);
        graph.add(next);

        defSet.put(lineno, arg1.dest.toString());
        
        List<String> used = new Vector<>();
        if(arg1.source instanceof VVarRef.Local) {
            used.add(arg1.source.toString());
        }
        useSet.put(lineno, used);

        lineno++; //Advance to next line.
    }

    @Override
    public void visit(VFunction arg0, VCall arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        next.add(lineno + 1);
        graph.add(next);

        defSet.put(lineno, arg1.dest.toString());

        List<String> used = new Vector<>();
        used.add(arg1.addr.toString());
        for(int i = 0; i < arg1.args.length; i++) {
            if(arg1.args[i] instanceof VVarRef.Local) {
                used.add(arg1.args[i].toString());
            }
        }

        useSet.put(lineno, used);

        lineno++;
    }

    @Override
    public void visit(VFunction arg0, VBuiltIn arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        next.add(lineno + 1);
        graph.add(next);

        if(arg1.dest != null) {
            defSet.put(lineno, arg1.dest.toString());
        } else {
            defSet.put(lineno, ""); // Error line is useless
        }

        List<String> used = new Vector<>();
        for(int i = 0; i < arg1.args.length; i++) {
            if(arg1.args[i] instanceof VVarRef.Local) {
                used.add(arg1.args[i].toString());
            }
        }

        useSet.put(lineno, used);

        lineno++;
    }

    @Override
    public void visit(VFunction arg0, VMemWrite arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        next.add(lineno + 1);
        graph.add(next);

        String dest = ((VMemRef.Global)arg1.dest).base.toString();

        defSet.put(lineno, ""); // No defined variables in memory writes

        List<String> used = new Vector<>();
        used.add(dest);
        if(arg1.source instanceof VVarRef.Local) {
            used.add(arg1.source.toString());
        }
        
        useSet.put(lineno, used);

        lineno++;
    }

    @Override
    public void visit(VFunction arg0, VMemRead arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        next.add(lineno + 1);
        graph.add(next);

        defSet.put(lineno, arg1.dest.toString()); // Variable defined in mem read. MUST be VVarRef, from API

        String src = ((VMemRef.Global)arg1.source).base.toString();
        List<String> used = new Vector<>();
        used.add(src);
        useSet.put(lineno, used);

        lineno++;
    }

    @Override
    public void visit(VFunction arg0, VBranch arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        next.add(lineno + 1);
        next.add(labelLines.get(arg1.target.ident));
        graph.add(next);

        defSet.put(lineno, ""); // No defined variables in branch

        List<String> used = new Vector<>();
        if(arg1.value instanceof VVarRef.Local) {
            used.add(arg1.value.toString());
        }
        useSet.put(lineno, used);

        lineno++;
    }

    @Override
    public void visit(VFunction arg0, VGoto arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        String label = arg1.target.toString().substring(1,arg1.target.toString().length());
        next.add(labelLines.get(label));
        graph.add(next);
        defSet.put(lineno, ""); // No defined variables in branch
        useSet.put(lineno, new Vector<>());
        lineno++;
    }

    @Override
    public void visit(VFunction arg0, VReturn arg1) throws RuntimeException {
        List<Integer> next = new Vector<>();
        graph.add(next); // No out edge for return

        defSet.put(lineno, ""); // No defined variables in return

        List<String> used = new Vector<>();
        if(arg1.value instanceof VVarRef.Local) {
            used.add(arg1.value.toString()); // check to make sure we are returning an actual value
        }
        useSet.put(lineno, used);
    }
    
    public void printGraph() {
        for(int i = 0; i < graph.size(); i++) {
            System.out.print("Line " + i + ": ");
            for(int j = 0; j < graph.get(i).size(); j++) {
                System.out.print("(" + i + "->" + graph.get(i).get(j) + ") ");
            }
            System.out.println(" Use: " + useSet.get(i) + " Def: " + defSet.get(i));
        }
    }
}
