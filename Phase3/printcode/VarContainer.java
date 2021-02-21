package printcode;

import java.util.*;


import cs132.vapor.ast.VVarRef.Local;
import registeralloc.*;


public class VarContainer {
    private int maxlocal; // ALL LOCALS WILL BE OFFSET BY THE NUMBER OF SREGS USED.
    private int Sreg_used; 
    private List<Interval> vals;
    private Set<Registers> sregs = new HashSet<>();
    public int inStack = 0;
    public int outStack = 0;
    public int localStack = 0;

    public VarContainer(List<Interval> tmp) {
        maxlocal = 0;
        Sreg_used = 0;
        vals = tmp;
        for(int i = 0; i < tmp.size(); i++) {
            Interval curr = tmp.get(i);
            if(curr.hasRegister()) {
                if(curr.register.getType().equals("s")) {
                    sregs.add(curr.register);
                }
            } else {
                maxlocal++;
            }
        }
        Sreg_used = sregs.size();
        maxlocal += Sreg_used;
        localStack += maxlocal;
    }

    public int getLocals() {
        return maxlocal;
    }

    public int numSregs() {
        return Sreg_used;
    }

    public int useLocal() {
        maxlocal++;
        return (maxlocal-1)+Sreg_used;
    }

    public boolean isInReg(String varname, int lineno) {
        Interval currval;
        for(int i = 0; i < vals.size(); i++) {
            currval = vals.get(i);
            if(currval.Var().equals(varname) && lineno >= currval.Start() && lineno <= currval.End()) {
                return currval.hasRegister();
            }
        }
        return false;
    }

    public String getActualOffset(Interval i) {
        return "" + (i.offset + Sreg_used);
    }

    public Interval getVarInterval(String varname, int lineno) {
        for(int i = 0; i < vals.size(); i++) {
            Interval currval = vals.get(i);
            if(currval.Var().equals(varname) && lineno >= currval.Start() && lineno <= currval.End()) {
                return currval;
            }
        }
        Interval tmpI = new Interval(varname, lineno);
        tmpI.setEnd(lineno);
        tmpI.register = new Registers("$v0", "v");
        return tmpI; // Only does this when variable is never used.
    }

    public String saveSregs() {
        String result = "";
        for(int i = 0; i < Sreg_used; i++) {
            result += "   local[" + i + "] = $s" + i + "\n";
        }
        return result;
    }

    public String restoreSregs() {
        String result = "";
        for(int i = 0; i < Sreg_used; i++) {
            result += "   $s" + i + " = local[" + i + "]\n";
        }
        return result;
    }

    public String generateArgs(Local[] args) {
        String result = "";
        int i;
        inStack = 0;
        if(args.length - 4 > 0) {
            inStack = args.length - 4;
        }
        for(i = 0; i < args.length && i < 4; i++) { // only handle args in regs
            Interval curr = getVarInterval(args[i].ident, 0);
            if(curr == null) continue; // Variable not used i guess?
            if(curr.hasRegister()) {
                result += "   " + curr.register.getReg() + " = $a" + i + "\n";
            } else {
                result += "   " + curr.location + "[" + (curr.offset + Sreg_used) + "]" + " = $a" + i + "\n";
            }
        }

        for(i = 4; i < args.length; i++) { // handle vals in the in stack. Assume remaining arguments are in in stack.
            Interval curr = getVarInterval(args[i].ident, 0);
            if(curr == null) continue; // Variable not used i guess?
            if(curr.hasRegister()) {
                result += "   " + curr.register.getReg() + " = in[" + (i-4) + "]\n";
            } else {
                result += "   $v0 = in[" + (i-4) + "]\n"
                + "   " + curr.location + "[" + (curr.offset + Sreg_used) + "] = $v0";
            }
        }
        return result;
    }
    
}
