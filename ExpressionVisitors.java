import visitor.GJDepthFirst;
import syntaxtree.*;

public class ExpressionVisitors extends GJDepthFirst<String,ContextType> {
    /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
    public String visit(AndExpression n, ContextType argu) {
        String p1 = n.f0.accept(this, argu);
        String p2 = n.f2.accept(this, argu);
        if(p1.equals("boolean") && p1.equals(p2)) {
            return p1;
        }
        throw new Error("Type error");
     }
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
     public String visit(CompareExpression n, ContextType argu) {
        String p1 = n.f0.accept(this, argu);
        String p2 = n.f2.accept(this, argu);
        if(p1.equals("int") && p1.equals(p2)) {
            return p1;
        }
        throw new Error("Type error");
     }
    
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
     public String visit(PlusExpression n, ContextType argu) {
        String p1 = n.f0.accept(this, argu);
        String p2 = n.f2.accept(this, argu);
        if(p1.equals("int") && p1.equals(p2)) {
            return p1;
        }
        throw new Error("Type error");
     }
    
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
     public String visit(MinusExpression n, ContextType argu) {
        String p1 = n.f0.accept(this, argu);
        String p2 = n.f2.accept(this, argu);
        if(p1.equals("int") && p1.equals(p2)) {
            return p1;
        }
        throw new Error("Type error");
     }

     /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
     public String visit(TimesExpression n, ContextType argu) {
        String p1 = n.f0.accept(this, argu);
        String p2 = n.f2.accept(this, argu);
        if(p1.equals("int") && p1.equals(p2)) {
            return p1;
        }
        throw new Error("Type error");
     } 
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
     public String visit(ArrayLookup n, ContextType argu) {
        String p1 = n.f0.accept(this, argu);
        String p2 = n.f2.accept(this, argu);
        if(p1.equals("int[]") && p2.equals("int")) {
            return p2;
        }
        throw new Error("Type error");
     }
     
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
     public String visit(ArrayLength n, ContextType argu) {
        String ptype = n.f0.accept(this, argu);
        if(!ptype.equals("int[]")) {
            throw new Error("Type error");
        }
        return "int";
     }
     /*
      TODO: #35
     */

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

     public String visit(PrimaryExpression n, ContextType argu) {
       
        String _ret = n.f0.accept(this,argu);
        return _ret;
     }
     
     /**
    * f0 -> <INTEGER_LITERAL>
    */
     public String visit(IntegerLiteral n) {

        String _ret = "int";
        return _ret;
     }
     /**
    * f0 -> "true"
    */
     public String visit(TrueLiteral n) {

        String _ret = "boolean";
        return _ret;
     }
     /**
    * f0 -> "false"
    */
     public String visit(FalseLiteral n) {

        String _ret = "boolean";
        return _ret;
     }
     //TODO #39
     /**
    * f0 -> <IDENTIFIER>
    */
     public String visit(Identifier n) {

        String _ret = null;
        //String t1 = n.f0.accept(this);
        return _ret;
     }
     //TODO #40
     /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n) {
        String _ret = null;
        //String t1 = n.f0.accept(this);
        return _ret;
     }
     /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
     public String visit(ArrayAllocationExpression n, ContextType argu) {
        String Atype = n.f3.accept(this, argu);
        if(!Atype.equals("int")) {
            throw new Error("Type error");
        }
        return "int[]";
     }
     //TODO #42
     /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
     public String visit(AllocationExpression n) {

        String _ret = null;
        //String t1 = n.f0.accept(this);
        return _ret;
     }
     /**
    * f0 -> "!"
    * f1 -> Expression()
    */
     public String visit(NotExpression n, ContextType argu) {
        String _ret = n.f1.accept(this, argu);
        if(!_ret.equals("boolean"))
        {
            throw new Error("Type error");
        }
        return _ret;
     }
     /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
     public String visit(BracketExpression n, ContextType argu) {
        return n.f1.accept(this, argu);
     }

}
