package printcode;

import cs132.vapor.ast.*;
import registeralloc.*;

public class PrintVisitor extends VInstr.VisitorPR<VarContainer, String, RuntimeException>{
    private int lineno = 0;
    private int outStack = 0;

    @Override
    public String visit(VarContainer arg0, VAssign arg1) throws RuntimeException {
        String destStr = arg1.dest.toString();
        String result = "";
        String LHS;
        String RHS;
        Interval lInt = arg0.getVarInterval(destStr, lineno);
        if(lInt.register != null && lInt.register.getReg().equals("$v0")) { // Not a used variable.
            lineno++;
            return "";
        }
        boolean needreg = false;
        if(arg0.isInReg(destStr, lineno)) {
            LHS = lInt.register.getReg();
        } else {
            LHS = lInt.location + "[" + arg0.getActualOffset(lInt) + "]";
            needreg = true;
        }

        if(arg1.source instanceof VVarRef.Local) {
            String srcStr = arg1.source.toString();
            lInt = arg0.getVarInterval(srcStr, lineno);
            if(arg0.isInReg(srcStr, lineno)) {
                RHS = lInt.register.getReg();
            } else {
                if(needreg) {
                    result += "$   v0 = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]";
                    RHS = "$v0";
                } else {
                    RHS = lInt.location + "[" + arg0.getActualOffset(lInt) + "]";
                }
            }
        } else {
            RHS = arg1.source.toString(); // its some literal.
        }
        

        result += "   " + LHS + " = " + RHS + "\n";
        lineno++;
        return result;
    }

    @Override
    public String visit(VarContainer arg0, VCall arg1) throws RuntimeException {
        String destStr = arg1.dest.toString();
        String result = "";
        String LHS;
        String RHS;
        Interval lInt = arg0.getVarInterval(destStr, lineno); // Handle the assigned variable
        if(arg0.isInReg(destStr, lineno) || lInt.register != null) {
            LHS = lInt.register.getReg();
        } else {
            LHS = lInt.location + "[" + arg0.getActualOffset(lInt) + "]";
        }

        // Handle args here.
        for(int i = 0; i < arg1.args.length && i < 4; i++) {
            if(arg1.args[i] instanceof VVarRef.Local) {
                String argStr = arg1.args[i].toString();
                lInt = arg0.getVarInterval(argStr, lineno);
                if(arg0.isInReg(argStr, lineno)) {
                    result += "   $a" + i + " = " + lInt.register.getReg() + "\n";
                } else {
                    result += "   $v0 = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]\n"
                    + "   $a" + i + " = $v0\n";
                }
            } else {
                result += "   $a" + i + " = " + arg1.args[i].toString() + "\n"; // Some literal.
            }
        }

        for(int i = 4; i < arg1.args.length; i++) {
            if(arg1.args[i] instanceof VVarRef.Local) {
                String argStr = arg1.args[i].toString();
                lInt = arg0.getVarInterval(argStr, lineno);
                if(arg0.isInReg(argStr, lineno)) {
                    result += "   out[" + (i-4) + "] = " + lInt.register.getReg() + "\n";
                } else {
                    result += "   $v0 = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]\n"
                    + "   out[" + (i-4) + "] = $v0\n";
                }
            } else {
                result += "   out[" + (i-4) + "] = " + arg1.args[i].toString() + "\n"; // Some literal.
            }
            if(i-4 >= outStack) {
                outStack++;
            }
        }



        if(arg1.addr instanceof VAddr.Var) {
            String callStr = arg1.addr.toString();
            lInt = arg0.getVarInterval(callStr, lineno);
            if(arg0.isInReg(callStr, lineno)) {
                RHS = lInt.register.getReg();
            } else {
                result += "   $v0 = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]\n";
                RHS = "$v0";
            }
        } else {
            RHS = arg1.addr.toString();
        }

        result += "   call " + RHS + "\n"
        + "   " + LHS + " = " + "$v0\n";
        arg0.outStack = outStack;
        lineno++;
        return result;
    }

    @Override
    public String visit(VarContainer arg0, VBuiltIn arg1) throws RuntimeException {
        if(arg1.dest == null) { // NO LHS
            String arguments = "";
            String result = "";

            for(int i = 0; i < arg1.args.length; i++) {
                if(arg1.args[i] instanceof VVarRef.Local) {
                    String argval = arg1.args[i].toString();
                    Interval rInt = arg0.getVarInterval(argval, lineno);
                    if(arg0.isInReg(argval, lineno)) {
                        arguments += " " + rInt.register.getReg();
                    } else {
                        result += "   $v" + i + " = " + rInt.location + "[" + arg0.getActualOffset(rInt) + "]\n";
                        arguments += " $v" + i;
                    }
                } else {
                    arguments += " " + arg1.args[i].toString();
                }
            }
            arguments = arguments.trim();

            result += "   " + arg1.op.name + "(" + arguments + ")\n";
            lineno++;
            return result;
        }

        //normal LHS and RHS
        String LHS;
        String arguments = "";
        String result = "";
        boolean leftStack = false;

        String destStr = arg1.dest.toString();
        Interval lInt = arg0.getVarInterval(destStr, lineno);
        if(arg0.isInReg(destStr, lineno)) {
            LHS = lInt.register.getReg();
        } else {
            LHS = "$v0";
            leftStack = true;
        }

        for(int i = 0; i < arg1.args.length; i++) {
            if(arg1.args[i] instanceof VVarRef.Local) {
                String argval = arg1.args[i].toString();
                Interval rInt = arg0.getVarInterval(argval, lineno);
                if(arg0.isInReg(argval, lineno)) {
                    arguments += " " + rInt.register.getReg();
                } else {
                    result += "   $v" + i + " = " + rInt.location + "[" + arg0.getActualOffset(rInt) + "]\n";
                    arguments += " $v" + i;
                }
            } else {
                arguments += " " + arg1.args[i].toString();
            }
        }
        arguments = arguments.trim();

        result += "   " + LHS + " = " + arg1.op.name + "(" + arguments + ")\n";

        if(leftStack) {
            result += "   " + lInt.location + "[" + arg0.getActualOffset(lInt) + "] = $v0\n";
        }

        lineno++;
        return result;
    }

    @Override
    public String visit(VarContainer arg0, VMemWrite arg1) throws RuntimeException {
        String destStr = ((VMemRef.Global)arg1.dest).base.toString();
        String LHS;
        String RHS;
        String result = "";
        int tempval = 0;
        int offset = ((VMemRef.Global)arg1.dest).byteOffset;

        Interval lInt = arg0.getVarInterval(destStr, lineno);
        if(arg0.isInReg(destStr, lineno)) {
            LHS = lInt.register.getReg();
        } else {
            result += "   $v" + tempval + " = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]\n";
            LHS = "$v" + tempval;
            tempval++;
        }

        if(arg1.source instanceof VVarRef.Local) {
            String srcStr = arg1.source.toString();
            lInt = arg0.getVarInterval(srcStr, lineno);
            if(arg0.isInReg(srcStr, lineno)) {
                RHS = lInt.register.getReg();
            } else {
                result += "   $v" + tempval + " = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]\n";
                RHS = "$v" + tempval;
            }
        } else {
            RHS = arg1.source.toString();
        }


        if(offset == 0) {
            result += "   [" + LHS + "] = " + RHS + "\n";
        } else {
            result += "   [" + LHS + "+" + offset + "] = " + RHS + "\n";
        }
        lineno++;
        return result;
    }

    @Override
    public String visit(VarContainer arg0, VMemRead arg1) throws RuntimeException {
        String LHS;
        String RHS;
        String result = "";
        int tempval = 0;

        String destStr = arg1.dest.toString();
        boolean putBackTemp = false;
        Interval lInt = arg0.getVarInterval(destStr, lineno);
        if(arg0.isInReg(destStr, lineno)) {
            LHS = lInt.register.getReg();
        } else {
            result += "   $v0 = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]\n";
            LHS = "$v0";
            tempval++;
            putBackTemp = true;
        }

        String srcStr = ((VMemRef.Global)arg1.source).base.toString();
        int offset = ((VMemRef.Global)arg1.source).byteOffset;
        Interval rInt = arg0.getVarInterval(srcStr, lineno);
        if(arg0.isInReg(srcStr, lineno)) {
            RHS = rInt.register.getReg();
        } else {
            result += "   $v" + tempval + " = " + rInt.location + "[" + arg0.getActualOffset(rInt) + "]\n";
            RHS = "$v" + tempval;
        }


        if(offset == 0) {
            result += "   " + LHS + " = [" + RHS + "]\n";
        } else {
            result += "   " + LHS + " = [" + RHS + "+" + offset + "]\n";
        }

        if(putBackTemp) {
            result += "   " + lInt.location + "[" + arg0.getActualOffset(lInt) + "] = $v0\n";
        }
        lineno++;
        return result;

    }

    @Override
    public String visit(VarContainer arg0, VBranch arg1) throws RuntimeException {
        String sval;
        String result = "";
        if(arg1.value instanceof VVarRef.Local) {
            String valStr = arg1.value.toString();
            Interval lVal = arg0.getVarInterval(valStr, lineno);
            if(arg0.isInReg(valStr, lineno)) {
                sval = lVal.register.getReg();
            } else {
                result += "   $v0 = " + lVal.location + "[" + arg0.getActualOffset(lVal) + "]\n";
                sval = "$v0";
            }
        } else {
            sval = arg1.value.toString(); // Literal.
        }

        if(arg1.positive) {
            result += "   if " + sval + " goto :" + arg1.target.ident + "\n";
        } else {
            result += "   if0 " + sval + " goto :" + arg1.target.ident + "\n";
        }
        lineno++;
        return result;
    }

    @Override
    public String visit(VarContainer arg0, VGoto arg1) throws RuntimeException {
        lineno++;
        return "   goto " + arg1.target.toString() + "\n";
    }

    @Override
    public String visit(VarContainer arg0, VReturn arg1) throws RuntimeException {
        String result = "";
        if(arg1.value != null) {
            if(arg1.value instanceof VVarRef.Local) {
                String retval = arg1.value.toString();
                Interval lInt = arg0.getVarInterval(retval, lineno);
                if(arg0.isInReg(retval, lineno)) {
                    result += "   $v0 = " + lInt.register.getReg() + "\n";
                } else {
                    result += "   $v0 = " + lInt.location + "[" + arg0.getActualOffset(lInt) + "]\n";
                }
            } else {
                result += "   $v0 = " + arg1.value.toString() + "\n";
            }
        }
        result += arg0.restoreSregs();
        result += "   ret\n";
        lineno++;
        return result;
    }
    
}
