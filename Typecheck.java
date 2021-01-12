import syntaxtree.*;
import java.util.*;

public class Typecheck {
    public static void main(String args[]) {
        try {
            new MiniJavaParser(System.in);
            Goal mainnode = MiniJavaParser.Goal(); // This is the TOP node of our syntax tree. Pass around as needed.
            System.out.println("Program type checked successfully");
        } catch (Throwable e) {
            //System.out.println("Syntax check failed: " + e.getMessage());
            System.out.println("Type error");
        }
    }
}