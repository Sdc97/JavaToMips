/*
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;
*/
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.*;
import java.util.*;

public class V2VM {
/* **************copied from the project web page 

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
*/

public static void main(String args[]) {
    try {
       
    } catch (Throwable e) {
        System.out.println(e.getMessage());
        System.exit(1);
    }
}
}