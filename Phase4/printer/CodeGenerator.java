package printer;

import cs132.vapor.ast.*;

public class CodeGenerator extends VInstr.VisitorPR<VFunction, String, RuntimeException>{

    @Override
    public String visit(VFunction arg0, VAssign arg1) throws RuntimeException {
        String result = "";
        if(arg1.source instanceof VLitInt) {
            // Argument is immediate
            result += "  li " + arg1.dest + " " + arg1.source + "\n";
        } else if (arg1.source instanceof VLabelRef) {
            result += "  la " + arg1.dest + " " + arg1.source.toString().substring(1) + "\n";
        } 
        else {
            // Argument is register
            result += "  move " + arg1.dest + " " + arg1.source + "\n";
        }
        return result;
    }

    @Override
    public String visit(VFunction arg0, VCall arg1) throws RuntimeException {
        if(arg1.addr instanceof VAddr.Var) {
            return "  jalr " + arg1.addr + "\n";
        } else {
            return "  jal " + arg1.addr.toString().substring(1) + "\n";
        }
    }

    @Override
    public String visit(VFunction arg0, VBuiltIn arg1) throws RuntimeException {
        String result = "";
        switch(arg1.op.name) {

            case "Add":
            // If both are not registers, simply load an addition of the literals, else use addu
            if(arg1.args[0] instanceof VLitInt && arg1.args[1] instanceof VLitInt) {
                result += "  li " + arg1.dest + " " + (Integer.parseInt(arg1.args[0].toString()) + Integer.parseInt(arg1.args[1].toString())) + "\n";
            } else if (arg1.args[0] instanceof VLitInt) {
                // First operand is a literal
                result += "  addu " + arg1.dest + " " + arg1.args[1] + " " + arg1.args[0] + "\n";
            } else {
                //Second operand is a literal, or neither.
                result += "  addu " + arg1.dest + " " + arg1.args[0] + " " + arg1.args[1] + "\n";
            }
            break;

            case "Sub":
            // If both are not registers, simply load a subtraction of the literals, else use subu 
            if(arg1.args[0] instanceof VLitInt && arg1.args[1] instanceof VLitInt) {
                result += "  li " + arg1.dest + " " + (Integer.parseInt(arg1.args[0].toString()) - Integer.parseInt(arg1.args[1].toString())) + "\n";
            } else if (arg1.args[0] instanceof VLitInt) {
                // First operand is a literal
                result += "  li $t9 " + arg1.args[0] + "\n";
                result += "  subu " + arg1.dest + " $t9 " + arg1.args[1] + "\n";
            } else {
                // Second operand is a literal, or neither.
                result += "  subu " + arg1.dest + " " + arg1.args[0] + " " + arg1.args[1] + "\n";
            }
            break;

            case "MulS":
            // If both are not registers, simply load a multiplication of the literals, else use mul 
            if(arg1.args[0] instanceof VLitInt && arg1.args[1] instanceof VLitInt) {
                result += "  li " + arg1.dest + " " + (Integer.parseInt(arg1.args[0].toString()) * Integer.parseInt(arg1.args[1].toString())) + "\n";
            } else if (arg1.args[0] instanceof VLitInt) {
                // First operand is a literal
                result += "  li $t9 " + arg1.args[0] + "\n";
                result += "  mul " + arg1.dest + " $t9 " + arg1.args[1] + "\n";
            } else {
                // second operand is a literal, or neither.
                result += "  mul " + arg1.dest + " " + arg1.args[0] + " " + arg1.args[1] + "\n";
            }
            break;

            case "Eq": // Don't think this is useful. We never generate any eq conditions, as we always use less than comparisons instead. May want to revisit this if there is issues. TODO
            break;

            case "Lt":
            // Handle first case where both are immediates:
            if(arg1.args[0] instanceof VLitInt && arg1.args[1] instanceof VLitInt) {
                result += "  li " + arg1.dest + " " + (Integer.parseInt(arg1.args[0].toString()) < Integer.parseInt(arg1.args[1].toString())) + "\n";
            } else if(arg1.args[1] instanceof VLitInt) {
                // Second operand is an immediate
                result += "  li $t9 " + arg1.args[1] + "\n";
                result += "  sltu " + arg1.dest + " " + arg1.args[0] + " $t9\n";
            } else if(arg1.args[0] instanceof VLitInt) {
                // First operand is an immediate
                result += "  li $t9 " + arg1.args[0] + "\n";
                result += "  sltu " + arg1.dest + " $t9 " + arg1.args[1] + "\n";
            } else {
                // Neither operand are immediates.
                result += "  sltu " + arg1.dest + " " + arg1.args[0] + " " + arg1.args[1] + "\n";
            }
            break;

            case "LtS":
            // Handle first case where both are immediates:
            if(arg1.args[0] instanceof VLitInt && arg1.args[1] instanceof VLitInt) {
                result += "  li " + arg1.dest + " " + (Integer.parseInt(arg1.args[0].toString()) < Integer.parseInt(arg1.args[1].toString())) + "\n";
            } else if(arg1.args[1] instanceof VLitInt) {
                // Second operand is an immediate
                result += "  li $t9 " + arg1.args[1] + "\n";
                result += "  slt " + arg1.dest + " " + arg1.args[0] + " $t9\n";
            } else if(arg1.args[0] instanceof VLitInt) {
                // First operand is an immediate
                result += "  li $t9 " + arg1.args[0] + "\n";
                result += "  slt " + arg1.dest + " $t9 " + arg1.args[1] + "\n";
            } else {
                // Neither operand are immediates.
                result += "  slt " + arg1.dest + " " + arg1.args[0] + " " + arg1.args[1] + "\n";
            }
            break;

            case "PrintIntS":
            if(arg1.args[0] instanceof VLitInt) {
                // Argument is a literal
                result += "  li $a0 " + arg1.args[0] + "\n";
            } else {
                // Argument is not a literal
                result += "  move $a0 " + arg1.args[0] + "\n";
            }
            // jal used, as we expect to return
            result += "  jal _print\n";
            break;

            case "HeapAllocZ":
            if(arg1.args[0] instanceof VLitInt) {
                // Argument is a literal
                result += "  li $a0 " + arg1.args[0] + "\n";
            } else {
                // Argument is not a literal
                result += "  move $a0 " + arg1.args[0] + "\n";
            }
            // jal used, as we expect to return
            result += "  jal _heapAlloc\n";
            result += "  move " + arg1.dest + " $v0\n";
            break;

            case "Error":
            if(arg1.args[0].toString().equals("\"null pointer\"")) {
                // Null pointer, so load null pointer string.
                result += "  la $a0 _str0\n";
            } else {
                // Array index out of bounds, so load array index string
                result += "  la $a0 _str1\n";
            }
            // only use j, because we wont be returning.
            result += "  j _error\n";
            break;
        }
        return result;
    }

    @Override
    public String visit(VFunction arg0, VMemWrite arg1) throws RuntimeException {
        String result = "";
        if(arg1.dest instanceof VMemRef.Global) {
            // Destination is of the form [reg + offset]
            if(arg1.source instanceof VLitInt) {
                // Argument is an immediate
                result += "  li $t9 " + arg1.source + "\n";
                result += "  sw $t9 " + ((VMemRef.Global)arg1.dest).byteOffset + "(" + ((VMemRef.Global)arg1.dest).base + ")\n";
            } else if (arg1.source instanceof VLabelRef) {
                // Argument is a label
                result += "  la $t9 " + arg1.source.toString().substring(1) + "\n";
                result += "  sw $t9 " + ((VMemRef.Global)arg1.dest).byteOffset + "(" + ((VMemRef.Global)arg1.dest).base + ")\n";
            } else {
                // Argument is a register
                result += "  sw " + arg1.source + " " + ((VMemRef.Global)arg1.dest).byteOffset + "(" + ((VMemRef.Global)arg1.dest).base + ")\n";
            }
        } else {
            // Destination is a stack member
            int stackoffset;
            String regbase;
            VMemRef.Stack tempval = ((VMemRef.Stack)arg1.dest);
            if(tempval.region.toString().equals("Out")) {
                // Out stack on LHS, so simply multiply stack index by 4.
                stackoffset = tempval.index * 4;
                regbase = "$sp";
            } else if (tempval.region.toString().equals("In")) {
                // In stack on LHS, so simply multiply stack index by 4
                stackoffset = tempval.index * 4;
                regbase = "$fp";
            } else {
                // Local stack on LHS, must add the size of out stack to offset.
                stackoffset = arg0.stack.out * 4 + tempval.index * 4;
                regbase = "$sp";
            }

            if(arg1.source instanceof VLitInt) {
                // Argument is an immediate
                result += "  li $t9 " + arg1.source + "\n";
                result += "  sw $t9 " + stackoffset + "(" + regbase + ")\n";
            } else if (arg1.source instanceof VLabelRef) {
                // Argument is a label
                result += "  la $t9 " + arg1.source.toString().substring(1) + "\n";
                result += "  sw $t9 " + stackoffset + "(" + regbase + ")\n";
            } else {
                // Argument is a register
                result += "  sw " + arg1.source + " " + stackoffset + "(" + regbase + ")\n";
            }
        }
        return result;
    }

    @Override
    public String visit(VFunction arg0, VMemRead arg1) throws RuntimeException {
        String result = "";
        int offset;
        String regbase;

        if(arg1.source instanceof VMemRef.Global) {
            VMemRef.Global tempval = (VMemRef.Global)arg1.source;
            offset = tempval.byteOffset;
            regbase = tempval.base.toString();
        } else {
            VMemRef.Stack tempval = (VMemRef.Stack)arg1.source;
            if(tempval.region.toString().equals("Out")) {
                // Out stack on LHS, so simply multiply stack index by 4.
                offset = tempval.index * 4;
                regbase = "$sp";
            } else if (tempval.region.toString().equals("In")) {
                // In stack on LHS, so simply multiply stack index by 4
                offset = tempval.index * 4;
                regbase = "$fp";
            } else {
                // Local stack on LHS, must add the size of out stack to offset.
                offset = arg0.stack.out * 4 + tempval.index * 4;
                regbase = "$sp";
            }
        }

        result += "  lw " + arg1.dest + " " + offset + "(" + regbase + ")\n";
        return result;
    }

    @Override
    public String visit(VFunction arg0, VBranch arg1) throws RuntimeException {
        String result = "";
        if(arg1.positive) {
            if(arg1.value instanceof VLitInt) {
                // argument is a literal
                result += "  li $t9 " + arg1.value + "\n";
                result += "  bnez $t9 " + arg1.target.toString().substring(1) + "\n";
            } else {
                result += "  bnez " + arg1.value + " " + arg1.target.toString().substring(1) + "\n";
            }
        } else {
            if(arg1.value instanceof VLitInt) {
                // argument is a literal
                result += "  li $t9 " + arg1.value + "\n";
                result += "  beqz $t9 " + arg1.target.toString().substring(1) + "\n";
            } else {
                result += "  beqz " + arg1.value + " " + arg1.target.toString().substring(1) + "\n";
            }
        }
        return result;
    }

    @Override
    public String visit(VFunction arg0, VGoto arg1) throws RuntimeException {
        return "  j " + arg1.target.toString().substring(1) + "\n";
    }

    @Override
    public String visit(VFunction arg0, VReturn arg1) throws RuntimeException {
        return "";
    }
    
}
