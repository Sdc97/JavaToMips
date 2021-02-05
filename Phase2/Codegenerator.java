
import visitor.GJDepthFirst;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import syntaxtree.*;

public class Codegenerator extends GJDepthFirst <String, ContextType> {

    
    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public String visit(Type n, ContextType argu) {
    String _ret = n.f0.accept(this, argu);
    return _ret;
    }

    /**
     * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(ArrayType n, ContextType argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
    */
    public String visit(BooleanType n, ContextType argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
    */
    public String visit(IntegerType n, ContextType argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    
    
    
    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, ContextType argu) {
        CodeIdContainer left = n.f0.accept(new CodeIdGenerator(), argu);
        CodeIdContainer tmp = n.f2.accept(new CodeIdGenerator(), argu);
        String _ret = "";
        // ADD example
        // tmp.code
        // id = Add(id1 id2)
        _ret += tmp.code  // assume that .code comes indented
        + argu.getTabs() + left.id + " = " + tmp.id + "\n"; 
        return _ret;
    }

    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, ContextType argu) {
        String _ret= "";
        String tmp = argu.newTemp();
        CodeIdContainer exp = n.f2.accept(new CodeIdGenerator(), argu);
        _ret = exp.code
        + argu.getTabs() + tmp + " = " + exp.id + "\n"
        + argu.getTabs() + Operations.PrintIntS(tmp) + "\n";
        return _ret;
    }
    
}

class CodeIdGenerator extends GJDepthFirst<CodeIdContainer,ContextType> {
    /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
    public CodeIdContainer visit(Expression n, ContextType argu) {
        return n.f0.accept(this,argu);
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
    public CodeIdContainer visit(AndExpression n, ContextType argu) {
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer result = new CodeIdContainer();
        String elseLable = argu.newLabel();
        String endLable = argu.newLabel();
        result.id = argu.newTemp();
        result.code = left.code + // assume newline handled inside
        argu.getTabs() + "if0 " + left.id + " goto :" + elseLable + "\n";
        argu.tabs++;

        CodeIdContainer right = n.f2.accept(this, argu);
        result.code += right.code
        + argu.getTabs() + result.id + "=" + right.id + "\n"
        + argu.getTabs() + "goto :" + endLable + "\n";
        argu.tabs--;
        
        result.code += argu.getTabs() + elseLable + ":" + "\n"
        + argu.getTabs() + "\t" + right.id + " = " + "0" + "\n"
        + argu.getTabs() + endLable + ":" + "\n";



        return result;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public CodeIdContainer visit(CompareExpression n, ContextType argu) {
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer right = n.f2.accept(this, argu);
        CodeIdContainer result = new CodeIdContainer();
        result.code = left.code + right.code;
        result.id =  Operations.LtS(left.id, right.id); // LtS(left.id right.id)
        return result;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public CodeIdContainer visit(PlusExpression n, ContextType argu) {
        
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer right = n.f2.accept(this, argu);
        CodeIdContainer result = new CodeIdContainer();
        result.code = left.code + right.code;
        result.id = Operations.Add(left.id, right.id); // Add(left.id right.id)
        return result;
    }
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public CodeIdContainer visit(MinusExpression n, ContextType argu) {
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer right = n.f2.accept(this, argu);
        CodeIdContainer result = new CodeIdContainer();
        result.code = left.code + right.code;
        result.id =  Operations.Sub(left.id, right.id); // Sub(left.id right.id)
        return result;
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public CodeIdContainer visit(TimesExpression n, ContextType argu) {
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer right = n.f2.accept(this, argu);
        CodeIdContainer result = new CodeIdContainer();
        result.code = left.code + right.code;
        result.id =  Operations.MulS(left.id, right.id); // MulS(left.id right.id)
        return result;
    }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public CodeIdContainer visit(ArrayLookup n, ContextType argu) {
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer right = n.f2.accept(this, argu);
        CodeIdContainer result = new CodeIdContainer();
        String t1 = argu.newTemp(); // size of the array
        String bounds = argu.newBoundsLabel();
        result.code = left.code + right.code
        + argu.getTabs() + t1 + " = " + "[" + left.id + "]" + "\n"
        + argu.getTabs() + t1 + " = " + Operations.Lt(right.id, t1) + "\n"
        + argu.getTabs() + "if " + t1 + " goto :" + bounds + "\n"
        + argu.getTabs() + "\tError(\"array index out of bounds\")" + "\n"
        + argu.getTabs() + bounds + ":" + "\n"
        + argu.getTabs() + t1 + " = " + Operations.MulS(right.id, "4") + "\n"
        + argu.getTabs() + t1 + " = " + Operations.Add(t1, left.id) + "\n";

        result.id = "[" + t1 + "+4]";

        return result;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public CodeIdContainer visit(ArrayLength n, ContextType argu) {
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer result = new CodeIdContainer();
        //String t1 = argu.newTemp(); // size of the array
        String nulllabel = argu.newNullLabel();
        result.code = left.code 
        + argu.getTabs() + "if " + left.id + " goto :" + nulllabel + "\n"
        + argu.getTabs() + "\t" +Operations.Error() + "\n"
        + argu.getTabs() + nulllabel + ":" + "\n";

        result.id = "[" + left.id + "]";
    return result;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public CodeIdContainer visit(MessageSend n, ContextType argu) {
        CodeIdContainer result = new CodeIdContainer();
        CodeIdContainer classToCall = n.f0.accept(this, argu); // base pointer to object in id.
        CodeIdContainer arguments = n.f4.accept(this, argu); // computed ids of arguments in form "arg1 arg2 arg3 ..."
        String classType = n.f0.accept(new ExpressionVisitors(), argu); // type of class
        String tmp = argu.newTemp();

        int methodOffset = ContextType.getMethodOffset(classType, n.f2.f0.tokenImage);

        result.code = classToCall.code + arguments.code;

        if(classToCall.id.equals("this")) {
            result.code += argu.getTabs() + tmp + " = [this]\n" // tmp points to v-table of object
            + argu.getTabs() + tmp + " = [" + tmp + "+" + methodOffset + "]\n";
            result.id = "call " + tmp + "(this " + arguments.id + ")\n";
        } else {
            String nulllabel = argu.newNullLabel();
            result.code +=  argu.getTabs() + "if " + classToCall.id + "goto :" + nulllabel + "\n"
            + argu.getTabs() + "\t" + Operations.Error() + "\n" 
            + argu.getTabs() + ":" + nulllabel + "\n"
            + argu.getTabs() + tmp + " = [" + classToCall.id + "]\n" // Sets tmp to base address of v-table
            + argu.getTabs() + tmp + " = [" + tmp + "+" + methodOffset + "]\n";
            result.id = "call " + tmp + "(" + classToCall.id + " " + arguments.id + ")\n"; 
        }

        return result;
    }

    public CodeIdContainer visit(NodeOptional n, ContextType argu) {
        if ( n.present() )
            return n.node.accept(this,argu);
        else
            return new CodeIdContainer();
    }

    /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
    public CodeIdContainer visit(ExpressionList n, ContextType argu) {
        CodeIdContainer result = new CodeIdContainer();
        CodeIdContainer left = n.f0.accept(this, argu);
        CodeIdContainer right = n.f1.accept(this, argu);
        String tmp = argu.newTemp();
        
        result.code = left.code + right.code
        + argu.getTabs() + tmp + " = " + left.id + "\n";
        
        result.id = tmp + right.id;
        return result;
    }

    public CodeIdContainer visit(NodeListOptional n, ContextType argu) {
        if ( n.present() ) {
        CodeIdContainer _ret= new CodeIdContainer();
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            CodeIdContainer tmp = e.nextElement().accept(this,argu);
            _ret.code += tmp.code;
            _ret.id += " " + tmp.id;
            _count++;
        }
        return _ret;
        }
        else
        return new CodeIdContainer();
    }

    /**
    * f0 -> ","
    * f1 -> Expression()
    */
    public CodeIdContainer visit(ExpressionRest n, ContextType argu) {
        CodeIdContainer result = new CodeIdContainer();
        CodeIdContainer right = n.f1.accept(this, argu);
        String tmp = argu.newTemp();
        result.code += right.code
        + argu.getTabs() + tmp + " = " + right.id + "\n";
        result.id = tmp;
        return result;
    }

    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
    public CodeIdContainer visit(PrimaryExpression n, ContextType argu) {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
   public CodeIdContainer visit(IntegerLiteral n, ContextType argu) {
    CodeIdContainer result = new CodeIdContainer();
    result.id = n.f0.tokenImage;
    return result;
    }

    /**
    * f0 -> "true"
    */
   public CodeIdContainer visit(TrueLiteral n, ContextType argu) {
    CodeIdContainer result = new CodeIdContainer();
    result.id = "1";
    return result;
    }

    /**
    * f0 -> "false"
    */
   public CodeIdContainer visit(FalseLiteral n, ContextType argu) {
    CodeIdContainer result = new CodeIdContainer();
    result.id = "0";
    return result;
    }

    /**
    * f0 -> <IDENTIFIER>
    */
   public CodeIdContainer visit(Identifier n, ContextType argu) {
    CodeIdContainer result = new CodeIdContainer();
    //String getType = argu.getTypeEnvType(n.f0.tokenImage);
    /* class A {
            int b;
            public int Arun(int b) {
                int b;
            }
    */ 
    result.code = "";
    if(argu.localIdent(n.f0.tokenImage)) // If identifier is a local var or method parameter
    {
        result.id = n.f0.tokenImage;
    }
    else { // else it must be a class parameter
        result.id = "[this+" + argu.findClassVarOffset(n.f0.tokenImage) +"]";
    }
    return result;
    }

    /**
    * f0 -> "this"
    */
   public CodeIdContainer visit(ThisExpression n, ContextType argu) {
    CodeIdContainer result = new CodeIdContainer();
    result.id = n.f0.tokenImage;
    return result;
    }

     /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public CodeIdContainer visit(ArrayAllocationExpression n, ContextType argu) {
    CodeIdContainer result = new CodeIdContainer();
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);
    n.f2.accept(this, argu);
    n.f3.accept(this, argu);
    n.f4.accept(this, argu);
    return result;
    }

    /**
     * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public CodeIdContainer visit(AllocationExpression n, ContextType argu) {
        CodeIdContainer result = new CodeIdContainer();
        result.id = argu.newTemp();
        result.code = argu.getTabs() + result.id + " = " + Operations.HeapAllocZ(ContextType.classVarField.get(n.f1.f0.tokenImage).entrySet().size() + 4) + "\n" // The amount of space needed is size of classVarField key map plus 4
        + argu.getTabs() + "[" + result.id + "]" + " = :vmt_" + n.f1.f0.tokenImage + "\n"; // Assign v-table to first memory location
        return result;
    }

    /**
     * f0 -> "!"
    * f1 -> Expression()
    */
    public CodeIdContainer visit(NotExpression n, ContextType argu) {
        CodeIdContainer result = new CodeIdContainer();
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return result;
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public CodeIdContainer visit(BracketExpression n, ContextType argu) {
        return n.f1.accept(this, argu);
     }

     //public CodeIdContainer visit(NodeToken n, ContextType argu) { return new CodeIdContainer();}

}

class CodeIdContainer {
    public String id = "";
    public String code = "";
}