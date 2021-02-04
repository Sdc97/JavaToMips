import visitor.*;
import syntaxtree.*;
import java.util.*;

public class UpperLevelVisitor extends GJVoidDepthFirst<ContextType>{

    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public void visit(Goal n, ContextType argu) {
        EarlyClassVisitor info = new EarlyClassVisitor();
        n.f0.accept(info, argu); // Fill out info in ContextType, checking for class name distinctness along the way.
        n.f1.accept(info, argu);
    }
    
}

// For filling out info in ContextType
class EarlyClassVisitor extends GJVoidDepthFirst<ContextType> {

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    public void visit(MainClass n, ContextType argu) { 
        ContextType.class_parents.put(n.f1.f0.tokenImage, "");
        ContextType.mainClass = n.f1.f0.tokenImage;
    }


    /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
    public void visit(TypeDeclaration n, ContextType argu) {
        n.f0.accept(this, argu);
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public void visit(ClassDeclaration n, ContextType argu) {
        if(ContextType.class_parents.containsKey(n.f1.f0.tokenImage)) { // class names must be distinct
            throw new Error("Type error");
        }
        ContextType.class_parents.put(n.f1.f0.tokenImage, ""); // Class with no parent mapped to empty string

        ContextType temp = new ContextType(); // Store current class in new contexttype
        temp.currclass = n.f1.f0.tokenImage;

        ContextType.classVarField.put(temp.currclass, new HashMap<String,String>()); // Setup map for class type environment. (fields(C))
        n.f3.accept(this, temp); // Fill class type environment, checking class variable distinctness. (19)
        ContextType.class_to_methods.put(temp.currclass, new HashMap<String,MethodDescriptor>());
        n.f4.accept(this, temp); // Create and fill MethodDescriptors, checking method distinctness. (19)
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    public void visit(ClassExtendsDeclaration n, ContextType argu) {
        if(ContextType.class_parents.containsKey(n.f1.f0.tokenImage)) { // class names must be distinct
            throw new Error("Type error");
        }
        ContextType.class_parents.put(n.f1.f0.tokenImage, n.f3.f0.tokenImage); // Store classes parent in ContextType

        ContextType temp = new ContextType(); // Store current class in new contexttype
        temp.currclass = n.f1.f0.tokenImage;

        ContextType.classVarField.put(temp.currclass, new HashMap<String,String>()); // Setup map for class type environment.
        n.f5.accept(this, temp); // Fill class type environment.
        ContextType.class_to_methods.put(temp.currclass, new HashMap<String,MethodDescriptor>());
        n.f6.accept(this, temp); // Create and fill MethodDescriptors, checking method distinctness. (19)
    }


    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public void visit(VarDeclaration n, ContextType argu) { // Should fill out class type envrionment in classVarField
        if(ContextType.classVarField.get(argu.currclass).containsKey(n.f1.f0.tokenImage)) { // If var name is not unique in class, type error.
            throw new Error("Type error");
        }
        // Retrieve string value of type from TypeEnvCreator, map var name to type in currclass scope.
        ContextType.classVarField.get(argu.currclass).put(n.f1.f0.tokenImage, n.f0.accept(new TypeEnvCreator(), argu));
    }

    /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    public void visit(MethodDeclaration n, ContextType argu) {
        MethodDescriptor current = new MethodDescriptor();
        current.return_type = n.f1.accept(new TypeEnvCreator(), argu); // get return type from TypeEnvCreator.
        current.argument_types = n.f4.accept(new MethodArgFill(), argu);
        
        if(ContextType.class_to_methods.get(argu.currclass).containsKey(n.f2.f0.tokenImage)) { // Method name must be unique. NO overloading.
            throw new Error("Type error");
        }
        ContextType.class_to_methods.get(argu.currclass).put(n.f2.f0.tokenImage, current); // Add MethodDescriptor.
    }
}


// Creates type environments and stores in given ContextType object
class TypeEnvCreator extends GJDepthFirst<String,ContextType> {
    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, ContextType argu) {
        if(argu.methodField.containsKey(n.f1.f0.tokenImage)) { // Method already contains duplicate declaration, throw error.
            throw new Error("Type error");
        }
        argu.methodField.put(n.f1.f0.tokenImage, n.f0.accept(this,argu));
        return null;
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    public String visit(Type n, ContextType argu) {
        String _ret=n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(ArrayType n, ContextType argu) {
        String _ret= "int[]";
        return _ret;
    }

    /**
     * f0 -> "boolean"
    */
    public String visit(BooleanType n, ContextType argu) {
        String _ret="boolean";
        return _ret;
    }

    /**
     * f0 -> "int"
    */
    public String visit(IntegerType n, ContextType argu) {
        String _ret="int";
        return _ret;
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, ContextType argu) {
        String _ret= n.f0.tokenImage;
        return _ret;
    }


}

// Used for creating the List<String> for MethodDescriptors
class MethodArgFill extends GJDepthFirst<List<String>, ContextType> {
    private List<String> cont = new Vector<String>();

    public List<String> visit(NodeOptional n, ContextType argu) {
        if ( n.present() )
           return n.node.accept(this,argu);
        else
           return cont;
     }

    /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
    public List<String> visit(FormalParameterList n, ContextType argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return cont;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    public List<String> visit(FormalParameter n, ContextType argu) {
        cont.add(n.f0.accept(new TypeEnvCreator(), argu));
        return null;
    }

    /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
    public List<String> visit(FormalParameterRest n, ContextType argu) {
        n.f1.accept(this, argu);
        return null;
    }

}
