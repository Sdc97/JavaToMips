import syntaxtree.*;
import visitor.GJDepthFirst;

public class StatementVisitor extends GJDepthFirst<String, ContextType> {
    private ExpressionVisitors expVisitor = new ExpressionVisitors();
    /**
     * f0 -> "{" 
     * f1 -> ( Statement() )* 
     * f2 -> "}"
     */
    public String visit(Block n, ContextType argu) {
        String _ret = null;
        n.f1.accept(this, argu); // Check all statements in the block to make sure they typecheck
        return _ret;
    }

    /**
     * f0 -> Identifier() 
     * f1 -> "=" 
     * f2 -> Expression() 
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, ContextType argu) {
        String _ret = null;
        String t1 = argu.getTypeEnvType(n.f0.f0.tokenImage); // Gets type of LHS
        String t2 = n.f2.accept(expVisitor, argu); // type of RHS expression
        if(!argu.isSubType(t1, t2)) { // CHECK that t2 is a subtype of t1 through ContextType.
            throw new Error("Type error");
        }
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
        String _ret=null;
        String idVal = argu.getTypeEnvType(n.f0.f0.tokenImage); // Get type of id with respect to the current type environment.
        if(!idVal.equals("int[]")) {
            throw new Error("Type error");
        }

        String e1 = n.f2.accept(expVisitor, argu); // Type of array indexing expression. Must be int.
        if(!e1.equals("int")) {
            throw new Error("Type error");
        }

        String e2 = n.f5.accept(expVisitor, argu); // Type of RHS expression. Must be int.
        if(!e2.equals("int")) {
            throw new Error("Type error");
        }

        return _ret;
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
        String _ret=null;
        String eVal = n.f2.accept(expVisitor, argu); // Type of the expression to check. Must be boolean.
        if(!eVal.equals("boolean")) {
            throw new Error("Type error");
        }

        n.f4.accept(this, argu); // Check remaining 2 statements to ensure they typecheck.
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, ContextType argu) {
        String _ret=null;
        String eVal = n.f2.accept(expVisitor, argu); // Type of the expression to check. Must be boolean.
        if(!eVal.equals("boolean")) {
            throw new Error("Type error");
        }

        n.f4.accept(this, argu); // Check internal statement to ensure it typechecks.
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
        String _ret=null;
        String eVal = n.f2.accept(expVisitor, argu); // Type of the expression to print. Must be int.

        if(!eVal.equals("int")) {
            throw new Error("Type error");
        }
        return _ret;
    }
}
