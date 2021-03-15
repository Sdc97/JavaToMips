
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import printer.CodeGenerator;
import cs132.vapor.ast.VDataSegment;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

import java.io.*;
import java.util.*;

public class VM2M {

  public static void main(String args[]) {
    try {
        VaporProgram temp = parseVapor(System.in, System.out); // Top node of our syntax tree.

        // Print out data segments.
        System.out.println(".data\n");
        for(int i = 0; i < temp.dataSegments.length; i++) {
          System.out.println(temp.dataSegments[i].ident + ":");
          for(int j = 0; j < temp.dataSegments[i].values.length; j++) {
            System.out.println("  " +temp.dataSegments[i].values[j].toString().substring(1));
          }
        }

        System.out.println("\n.text\n");
        System.out.println("  jal Main\n  li $v0 10\n  syscall\n");

        for(int i = 0; i < temp.functions.length; i++) {
          //Enter function, print name, then perform stack logistics.
          System.out.println(temp.functions[i].ident + ":");
          System.out.println("  sw $fp -8($sp)");
          System.out.println("  move $fp $sp");
          int offset = (temp.functions[i].stack.local + temp.functions[i].stack.out + 2) * 4;
          System.out.println("  subu $sp $sp " + offset);
          System.out.println("  sw $ra -4($fp)");

          // Create label map - used for printing out labels on correct lines when iterating through VInstr
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

          // Handle all instructions, save code in result
          String result = "";
          for(int j = 0; j < temp.functions[i].body.length; j++) {
            if(labels.containsKey(j)) {
              for(int k = 0; k < labels.get(j).size(); k++) {
                result += labels.get(j).get(k) + ":\n";
              }
            }
            result += temp.functions[i].body[j].accept(temp.functions[i], new CodeGenerator());
          }
          System.out.print(result);

          // Exit function, perform exit logistics
          System.out.println("  lw $ra -4($fp)");
          System.out.println("  lw $fp -8($fp)");
          System.out.println("  addu $sp $sp " + offset);
          System.out.println("  jr $ra");

          System.out.println();
        }
        // Prints utility code for every mips program.
        printUtility();
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
    boolean allowLocals = false;
    String[] registers = {
      "v0", "v1",
      "a0", "a1", "a2", "a3",
      "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
      "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
      "t8",
    };
    boolean allowStack = true;

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

  private static void printUtility() {
    System.out.println("_print:\n  li $v0 1\n  syscall\n  la $a0 _newline\n  li $v0 4\n  syscall\n  jr $ra\n");
    System.out.println("_error:\n  li $v0 4\n  syscall\n  li $v0 10\n  syscall\n");
    System.out.println("_heapAlloc:\n  li $v0 9\n  syscall\n  jr $ra\n");
    System.out.println(".data\n.align 0\n_newline: .asciiz \"\\n\"\n_str0: .asciiz \"null pointer\\n\"\n_str1: .asciiz \"array index out of bounds\\n\"");
  }
}