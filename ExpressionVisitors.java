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
        if(p1.equals("int[]") && p1.equals(p2)) {
            return p1;
        }
        throw new Error("Type error");
     }
     
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
     public String visit(ArrayLength n, ContextType argu) {
        String p1 = n.f0.accept(this, argu);
        //String p2 = n.f2.accept(this, argu);
        if(p1.equals("int[]")) {
            return p1;
        }
        throw new Error("Type error");
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
   // public String visit(PrimaryExpression n, ContextType argu) {

    // }
      
   
}
