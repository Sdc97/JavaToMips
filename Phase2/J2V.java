import syntaxtree.*;
import java.util.*;

public class J2V {
    public static void main(String args[]) {
        try {
            new MiniJavaParser(System.in);
            Goal mainnode = MiniJavaParser.Goal(); // This is the TOP node of our syntax tree. Pass around as needed.
            mainnode.accept(new UpperLevelVisitor(), null);
            VTableCreator.CreateVTables();
            VTableCreator.varClassOffsetMapping();
            System.out.println(mainnode.accept(new Codegenerator(), null));
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
