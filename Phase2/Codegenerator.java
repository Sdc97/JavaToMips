
import visitor.GJDepthFirst;

import java.util.*;

import syntaxtree.*;

public class Codegenerator extends GJDepthFirst <String, ContextType> {

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public String visit(Goal n, ContextType argu) {
        String _ret= n.f0.accept(this, argu) + n.f1.accept(this, argu);
        _ret += "func AllocArray(size)\n"
                + "   bytes = MulS(size 4)\n"
                + "   bytes = Add(bytes 4)\n"
                + "   v = HeapAllocZ(bytes)\n"
                + "   [v] = size\n"
                + "   ret v\n";
        return _ret; // should also make the array alloc func
    }

    /**
     * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    public String visit(MainClass n, ContextType argu) {
        ContextType cc = new ContextType();
        n.accept(new UpperLevelVisitor(), cc); // handles class type env
        cc.methodArgField  = new HashMap<>(); // create this so no segfaults
        String result = "func Main()\n";
        cc.tabs++;
        result += n.f15.accept(this, cc)
        + cc.getTabs() + "ret\n\n";
        cc.tabs--;
        return result;
    }

    /**
     * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
    public String visit(TypeDeclaration n, ContextType argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public String visit(ClassDeclaration n, ContextType argu) {
        ContextType cc = new ContextType();
        cc.currclass = n.f1.f0.tokenImage;

        String result = n.f4.accept(this, cc);
        return result;
    }

    /**
     * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    public String visit(ClassExtendsDeclaration n, ContextType argu) {
        ContextType cc = new ContextType();
        cc.currclass = n.f1.f0.tokenImage;

        String result = n.f6.accept(this, cc);
        return result;
    }

    /**
     * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    public String visit(MethodDeclaration n, ContextType argu) {
        ContextType mm = new ContextType();
        mm.currclass = argu.currclass;
        
        n.accept(new UpperLevelVisitor(), mm);
        String args = n.f4.accept(this, mm); // arguments in the form " arg1 arg2 arg3 ..."
        if(args == null) args = "";
        String result = "func " + mm.currclass + "." + n.f2.f0.tokenImage + "(this" + args + ")\n";
        mm.tabs++; 
        result += n.f8.accept(this, mm);
        CodeIdContainer retexp = n.f10.accept(new CodeIdGenerator(), mm);
        String tmp1 = mm.newTemp();
        result += retexp.code
        + mm.getTabs() + tmp1 + " = " + retexp.id + "\n"
        + mm.getTabs() + "ret " + tmp1 + "\n\n";
        mm.tabs--;

        return result;
    }

    /**
     * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
    public String visit(FormalParameterList n, ContextType argu) {
        String result = n.f0.accept(this, argu) + n.f1.accept(this, argu);
        return result;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    public String visit(FormalParameter n, ContextType argu) {
        String result = " " + n.f1.f0.tokenImage;
        return result;
    }

    /**
     * f0 -> ","
    * f1 -> FormalParameter()
    */
    public String visit(FormalParameterRest n, ContextType argu) {
        return n.f1.accept(this, argu);
    }

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
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
    public String visit(Statement n, ContextType argu) {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
    public String visit(Block n, ContextType argu) {
        return n.f1.accept(this, argu);
    }

    // Need this for the above method.
    public String visit(NodeListOptional n, ContextType argu) {
        if ( n.present() ) {
        String _ret= "";
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            _ret += e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
        }
        else
        return "";
    }
    
    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, ContextType argu) {
        String left;
        CodeIdContainer tmp = n.f2.accept(new CodeIdGenerator(), argu);
        String _ret = "";
        if(argu.localIdent(n.f0.f0.tokenImage)) // If identifier is a local var or method parameter
        {
            left = n.f0.f0.tokenImage;
        }
        else { // else it must be a class parameter
            left = "[this+" + argu.findClassVarOffset(n.f0.f0.tokenImage) +"]";
        }
        _ret += tmp.code  // assume that .code comes indented
        + argu.getTabs() + left + " = " + tmp.id + "\n"; 
        return _ret;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    public String visit(ArrayAssignmentStatement n, ContextType argu) {
        String result = "";
        String tmp1 = argu.newTemp();
        String tmp2 = argu.newTemp();
        String nullLabel = argu.newNullLabel();
        String boundLabel = argu.newBoundsLabel();
        CodeIdContainer idval = n.f0.accept(new CodeIdGenerator(), argu); // base address of array
        CodeIdContainer indexval = n.f2.accept(new CodeIdGenerator(), argu); // id of index value
        CodeIdContainer rhsExp = n.f5.accept(new CodeIdGenerator(), argu);
        result += idval.code 
        + argu.getTabs() + tmp1 + " = " + idval.id + "\n"
        + argu.getTabs() + "if " + tmp1 + " goto :" + nullLabel + "\n"
        + argu.getTabs() + "   Error(\"null pointer\")\n"
        + argu.getTabs() + nullLabel + ":\n"
        + argu.getTabs() + tmp2 + " = [" + tmp1 + "]\n"
        + indexval.code
        + argu.getTabs() + tmp2 + " = " + Operations.Lt(indexval.id, tmp2) + "\n"
        + argu.getTabs() + "if " + tmp2 + " goto :" + boundLabel +"\n"
        + argu.getTabs() + "   Error(\"array index out of bounds\")\n"
        + argu.getTabs() + boundLabel + ":\n"
        + argu.getTabs() + tmp2 + " = " + Operations.MulS(indexval.id, "4") + "\n"
        + argu.getTabs() + tmp2 + " = " + Operations.Add(tmp2, tmp1) + "\n"
        + rhsExp.code
        + argu.getTabs() + "[" + tmp2 + "+4] = " + rhsExp.id + "\n";
        return result;
    }

    /**
     * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    public String visit(IfStatement n, ContextType argu) {
        String result = "";
        CodeIdContainer checkexp = n.f2.accept(new CodeIdGenerator(), argu);
        String ifelselabel = argu.newIfElseLabel();
        String ifendlabel = argu.newIfEndLabel();
        result += checkexp.code
        + argu.getTabs() + "if0 " + checkexp.id + " goto :" + ifelselabel + "\n";
        argu.tabs++;
        String ifstr = n.f4.accept(this, argu);
        result += ifstr
        + argu.getTabs() + "goto :" + ifendlabel + "\n";
        argu.tabs--;
        result += argu.getTabs() + ifelselabel + ":\n";
        argu.tabs++;
        String elsestr = n.f6.accept(this, argu);
        result += elsestr;
        argu.tabs--;
        result += argu.getTabs() + ifendlabel + ":\n";
        return result;
    }

    /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, ContextType argu) {
        String result = "";
        String beginlabel = argu.newWhileBeginLabel();
        String endlabel = argu.newWhileEndLabel();
        CodeIdContainer exp = n.f2.accept(new CodeIdGenerator(), argu);
        result += argu.getTabs() + beginlabel + ":\n"
        + exp.code
        + argu.getTabs() + "if0 " + exp.id + " goto :" + endlabel + "\n";
        argu.tabs++;
        String stmts = n.f4.accept(this, argu);
        result += stmts
        + argu.getTabs() + "goto :" + beginlabel + "\n";
        argu.tabs--;
        result += argu.getTabs() + endlabel + ":\n";

        return result;
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
        CodeIdContainer exp = n.f2.accept(new CodeIdGenerator(), argu);
        _ret = exp.code
        + argu.getTabs() + Operations.PrintIntS(exp.id) + "\n";
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
        String elseLable = argu.newAndElseLabel();
        String endLable = argu.newAndEndLabel();
        result.id = argu.newTemp();
        String tmp1 = argu.newTemp();
        result.code = left.code // assume newline handled inside
        + argu.getTabs() + tmp1 + " = " + left.id + "\n"
        + argu.getTabs() + "if0 " + tmp1 + " goto :" + elseLable + "\n"; // left.id = Sub(arg1 arg2)
        argu.tabs++;

        CodeIdContainer right = n.f2.accept(this, argu);
        result.code += right.code
        + argu.getTabs() + result.id + " = " + right.id + "\n"
        + argu.getTabs() + "goto :" + endLable + "\n";
        argu.tabs--;
        
        result.code += argu.getTabs() + elseLable + ":\n"
        + argu.getTabs() + "   " + result.id + " = 0\n"
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
        result.id = argu.newTemp();
        result.code = left.code + right.code
        + argu.getTabs() + result.id + " = " + Operations.LtS(left.id, right.id) + "\n";
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
        result.id = argu.newTemp();
        result.code = left.code + right.code
        + argu.getTabs() + result.id + " = " + Operations.Add(left.id, right.id) + "\n"; // Add(left.id right.id)
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
        result.id = argu.newTemp();
        result.code = left.code + right.code
        + argu.getTabs() + result.id + " = " + Operations.Sub(left.id, right.id) + "\n"; // Sub(left.id right.id)
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
        result.id = argu.newTemp();
        result.code = left.code + right.code
        + argu.getTabs() + result.id + " = " + Operations.MulS(left.id, right.id) + "\n"; // MulS(left.id right.id)
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
        result.id = argu.newTemp();
        result.code = left.code + right.code
        + argu.getTabs() + t1 + " = " + "[" + left.id + "]" + "\n"
        + argu.getTabs() + t1 + " = " + Operations.Lt(right.id, t1) + "\n"
        + argu.getTabs() + "if " + t1 + " goto :" + bounds + "\n"
        + argu.getTabs() + "   Error(\"array index out of bounds\")" + "\n"
        + argu.getTabs() + bounds + ":" + "\n"
        + argu.getTabs() + t1 + " = " + Operations.MulS(right.id, "4") + "\n"
        + argu.getTabs() + t1 + " = " + Operations.Add(t1, left.id) + "\n"
        + argu.getTabs() + result.id + " = " + "[" + t1 + "+4]\n";

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
        + argu.getTabs() + "   " +Operations.Error() + "\n"
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
        result.id = argu.newTemp();

        int methodOffset = ContextType.getMethodOffset(classType, n.f2.f0.tokenImage);

        result.code = classToCall.code + arguments.code;

        if(classToCall.id.equals("this")) {
            result.code += argu.getTabs() + tmp + " = [this]\n" // tmp points to v-table of object
            + argu.getTabs() + tmp + " = [" + tmp + "+" + methodOffset + "]\n"
            + argu.getTabs() + result.id + " = call " + tmp + "(this" + arguments.id + ")\n";
        } else {
            String nulllabel = argu.newNullLabel();
            result.code +=  argu.getTabs() + "if " + classToCall.id + " goto :" + nulllabel + "\n"
            + argu.getTabs() + "   " + Operations.Error() + "\n" 
            + argu.getTabs() + nulllabel + ":\n"
            + argu.getTabs() + tmp + " = [" + classToCall.id + "]\n" // Sets tmp to base address of v-table
            + argu.getTabs() + tmp + " = [" + tmp + "+" + methodOffset + "]\n"
            + argu.getTabs() + result.id + " = call " + tmp + "(" + classToCall.id + arguments.id + ")\n"; 
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
        
        result.id = " " + tmp + right.id;
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
        result.id = argu.newTemp();
        result.code = argu.getTabs() + result.id + " = " + "[this+" + argu.findClassVarOffset(n.f0.tokenImage) +"]\n"; // Must be like this to work with other expressions and such. Yes it sucks.
        //result.id = "[this+" + argu.findClassVarOffset(n.f0.tokenImage) +"]";
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
    CodeIdContainer exp = n.f3.accept(this, argu);
    result.id = argu.newTemp();
    result.code = exp.code
    + argu.getTabs() + result.id + " = call :AllocArray(" + exp.id + ")\n"; // new to have the AllocArray function at the bottom for this to work!
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
        result.code = argu.getTabs() + result.id + " = " + Operations.HeapAllocZ(ContextType.classVarOffsets.get(n.f1.f0.tokenImage).keySet().size()*4 + 4) + "\n" // The amount of space needed is size of classVarField key map plus 4
        + argu.getTabs() + "[" + result.id + "]" + " = :vmt_" + n.f1.f0.tokenImage + "\n"; // Assign v-table to first memory location
        return result;
    }

    /**
     * f0 -> "!"
    * f1 -> Expression()
    */
    public CodeIdContainer visit(NotExpression n, ContextType argu) { // supose !(v1 < v2)
        CodeIdContainer result = new CodeIdContainer();
        CodeIdContainer right = n.f1.accept(this, argu);
        result.id = argu.newTemp();
        result.code = right.code
        + argu.getTabs() + result.id + " = " + Operations.Sub("1", right.id) + "\n";
        return result;
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public CodeIdContainer visit(BracketExpression n, ContextType argu) {
        /*
        CodeIdContainer result = new CodeIdContainer();
        CodeIdContainer right = n.f1.accept(this, argu); // id or temp OR Add(arg1 arg2)
        String tmp = argu.newTemp();
        result.code = right.code
        + argu.getTabs() + tmp + " = " + right.id + "\n";
        result.id = tmp;
        */
        return n.f1.accept(this, argu); // return result? Keeping above code just in case.
     }

}

class CodeIdContainer {
    public String id = "";
    public String code = "";
}