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
}
