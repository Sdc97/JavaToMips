
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;
import liveness.*;
import registeralloc.*;

import java.io.*;
import java.util.*;

public class V2VM {

  public static void main(String args[]) {
    try {
        VaporProgram temp = parseVapor(System.in, System.out);
        for(int i = 0; i < temp.functions.length; i++) {
          List<Interval> tmp = new IntervalCreation().createIntervals(temp.functions[i]);
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