import syntaxtree.*;
import java.util.*;

public class Typecheck {
    public static void main(String args[]) {
        try {
            new MiniJavaParser(System.in);
            Goal mainnode = MiniJavaParser.Goal(); // This is the TOP node of our syntax tree. Pass around as needed.
            UpperLevelVisitor temp = new UpperLevelVisitor();
            mainnode.accept(temp, null);
            System.out.println("Program type checked successfully");
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}