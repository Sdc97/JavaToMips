
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;
import liveness.*;
import printcode.PrintVisitor;
import printcode.VarContainer;
import registeralloc.*;

import java.io.*;
import java.util.*;

public class V2VM {

  public static void main(String args[]) {
    try {
        VaporProgram temp = parseVapor(System.in, System.out);
        for(int i = 0; i < temp.dataSegments.length; i++) {
          VDataSegment tmpD = temp.dataSegments[i];
          System.out.println("const " + tmpD.ident);
          for(int j = 0; j < tmpD.values.length; j++) {
            System.out.println("   " + tmpD.values[j].toString());
          }
          System.out.println();
        }
        for(int i = 0; i < temp.functions.length; i++) { // for each function
          //if(!temp.functions[i].ident.equals("Tree.Compare")) continue; // DEBUG
          List<Interval> tmp = new IntervalCreation().createIntervals(temp.functions[i]);
          Algorithm regalloc = new Algorithm();
          regalloc.assignJList(tmp);
          regalloc.LinearScanRegisterAllocation();

          /*
          for(int j = 0; j < tmp.size(); j++) { // DEBUG LOOP
            Interval tmpI = tmp.get(j);
            System.out.println("Variable name: " + tmpI.Var() + " Start: " + tmpI.Start() + " End: " + tmpI.End());
            if(tmpI.hasRegister()) {
              System.out.println("Register: " + tmpI.register.getReg());
            } else {
              System.out.println("Location: " + tmpI.location + " Offset: " + tmpI.offset);
            }
          }
          */
          
          
          
          
          Map<Integer,List<String>> labels = new HashMap<>();
          for(int k = 0; k < temp.functions[i].labels.length; k++) {
            if(labels.containsKey(temp.functions[i].labels[k].instrIndex)) {
              labels.get(temp.functions[i].labels[k].instrIndex).add(temp.functions[i].labels[k].ident);
            } else {
              List<String> currlabels = new ArrayList<>();
              currlabels.add(temp.functions[i].labels[k].ident);
              labels.put(temp.functions[i].labels[k].instrIndex, currlabels);
            }
          }
          VarContainer currFunc = new VarContainer(tmp);
          PrintVisitor tmpV = new PrintVisitor();
          String result = currFunc.saveSregs() + currFunc.generateArgs(temp.functions[i].params);
          for(int j = 0; j < temp.functions[i].body.length; j++) {
              if(labels.containsKey(j)) {
                for(int k = 0; k < labels.get(j).size(); k++) {
                  result += labels.get(j).get(k) + ":\n";
                }
              }
              //System.out.println("FUNC " + temp.functions[i].ident + " LINE " + j);
              result += temp.functions[i].body[j].accept(currFunc, tmpV);
          }
          System.out.println("func " + temp.functions[i].ident + "[in " + currFunc.inStack + ", out " + currFunc.outStack + ", local " + currFunc.localStack + "]");
          System.out.println(result);
        }
    } catch (Throwable e) {
        System.out.println(e.getMessage());
        System.exit(1);
    }
  }

  public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
    Op[] ops = {
      Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
      Op.PrintIntS, Op.HeapAllocZ, Op.Error,
    };
    boolean allowLocals = true;
    String[] registers = null;
    boolean allowStack = false;

    VaporProgram tree;
    try {
      tree = VaporParser.run(new InputStreamReader(in), 1, 1,
                            java.util.Arrays.asList(ops),
                            allowLocals, registers, allowStack);
    }
    catch (ProblemException ex) {
      err.println(ex.getMessage());
      return null;
    }

    return tree;
  }
}