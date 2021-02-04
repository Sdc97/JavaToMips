

import visitor.GJDepthFirst;

import java.util.List;
import java.util.Vector;

import syntaxtree.*;

public class Codegenerator extends GJDepthFirst <String, ContextType> {

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, ContextType argu) {
        CodeIdContainer tmp = n.f2.accept(new CodeIdGenerator(), argu);
        String _ret = "";
        // ADD example
        // tmp.code
        // id = Add(id1 id2)
        _ret += tmp.code  // assume that .code comes indented
        + argu.getTabs() + n.f0.f0.tokenImage + " = " + tmp.id + "\n"; 
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
    if(argu.localIdent(n.f0.tokenImage))
    {
        result.id = n.f0.tokenImage;
    }
    return result;
    }

    /**
    * f0 -> "this"
    */
   public CodeIdContainer visit(ThisExpression n, ContextType argu) {
    CodeIdContainer result = new CodeIdContainer();
    result.id = n.f0.tokenImage;
    result.code = argu.currclass;
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



}

class CodeIdContainer {
    public String id = "";
    public String code = "";
}