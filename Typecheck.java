import visitor.DepthFirstVisitor;
import syntaxtree.*;
import java.util.*;

public class Typecheck extends DepthFirstVisitor {
    public static void main(String args[]) {
        try {
            new MiniJavaParser(System.in);
            Goal mainnode = MiniJavaParser.Goal(); // This is the TOP node of our syntax tree. Pass around as needed.
            TypeCheckHelper checker = new TypeCheckHelper();
            checker.visit(mainnode);
            if(!checker.Goalcheck(mainnode)) {
                throw new Error("Type checking failed.");
            }
            System.out.println("Program type checked sucessfully");
        } catch (Throwable e) {
            //System.out.println("Syntax check failed: " + e.getMessage());
            System.out.println("Type error");
        }
    }
}

// We are going to make our own class that EXTENDS the given visitors.
