import syntaxtree.*;
import java.util.*;

public class J2V {
    public static void main(String args[]) {
        new MiniJavaParser(System.in);
        Goal mainnode = MiniJavaParser.Goal(); // This is the TOP node of our syntax tree. Pass around as needed.
        UpperLevelVisitor temp = new UpperLevelVisitor();
        
        mainnode.accept(temp, null);
        mainnode.accept(null,null);
    }
}
