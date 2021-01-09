import visitor.DepthFirstVisitor;
import syntaxtree.*;
import java.util.*;

public class TypeCheckHelper extends DepthFirstVisitor{
    /*
     * I think we will probably need a 2 pass system here, as we will need contextual information about each method and such
     * as in the class it exists in and its scope.
     * 
    */
    // Helper functions

    public String classname(MainClass n) {
        return n.f1.f0.tokenImage;
    }

    public String classname(ClassDeclaration n) {
        return n.f1.f0.tokenImage;
    }

    public String classname(ClassExtendsDeclaration n) {
        return n.f1.f0.tokenImage;
    }

    public String methodname(MethodDeclaration n) {
        return n.f2.f0.tokenImage;
    }

    public boolean distinct(List<String> t) {
        for (int i = 0; i < t.size(); i++) {
            for (int j = i + 1; j < t.size(); j++) {
                if (t.get(i).equals(t.get(j)))
                    return false;
            }
        }
        return true;
    }


    public boolean fields() {
         return true;
    } 


    //Check functions

    List<ClassDeclaration> regular_classes = new Vector<ClassDeclaration>();
    List<ClassExtendsDeclaration> extended_classes = new Vector<ClassExtendsDeclaration>();
    /**
     * Goal
     * @param n
     * @return true/false. Requires The MainClass object, and the objects of all classes declared. Can obtain mainclass directly from goal, others will 
     * be obtained from visitor.
     */
    public boolean Goalcheck(Goal n) {
        List<String> classnames = new Vector<String>();
        classnames.add(classname(n.f0));
        for(ClassDeclaration i : regular_classes) {
            classnames.add(i.f1.f0.tokenImage);
        }
        for(ClassExtendsDeclaration i : extended_classes) {
            classnames.add(i.f1.f0.tokenImage);
        }
        if(!distinct(classnames)) { // Class ids are distinct
            return false;
        }
        if(!MainClassCheck(n.f0)) { // Main class type checks
            return false;
        }
        for(ClassDeclaration i : regular_classes) { // Regular classes type check.
            if(!RegularClassCheck(i)) {
                return false;
            }
        }
        for(ClassExtendsDeclaration i : extended_classes) { // Extended classes type check
            if(!ExtendedClassCheck(i)) {
                return false;
            }
        }
        return true;
    }

    Map<String,Vector<VarDec>> classVarDecs = new HashMap<String,Vector<VarDec>>();
    /**
     * MainClassCheck
     * @param n
     * @return true if all hypotheses pass, false otherwise. Requies the type environment of the main class,
     */
    public boolean MainClassCheck(MainClass n) { // TODO finish implementation of this function
        Vector<VarDec> curr = classVarDecs.get(n.f1.f0.tokenImage);
        List<String> ids = new Vector<String>();
        for(VarDec i : curr) {
            ids.add(i.id);
        }
        if(!distinct(ids)) {
            return false;
        }
        return true;
    }

    public boolean RegularClassCheck(ClassDeclaration n) {
        return true;
    }

    public boolean ExtendedClassCheck(ClassExtendsDeclaration n) {
        return true;
    }



    // Visitor functions

    /**
     * f0 -> MainClass() 
     * f1 -> ( TypeDeclaration() )* 
     * f2 -> <EOF>
     */
    public void visit(Goal n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

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
    public void visit(MainClass n) {
        currClass = n.f1.f0.tokenImage;
        classVarDecs.put(currClass,new Vector<VarDec>());
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
        n.f8.accept(this);
        n.f9.accept(this);
        n.f10.accept(this);
        n.f11.accept(this);
        n.f12.accept(this);
        n.f13.accept(this);
        n.f14.accept(this);
        n.f15.accept(this);
        n.f16.accept(this);
        n.f17.accept(this);
    }

    /**
     * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
    public void visit(TypeDeclaration n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public void visit(ClassDeclaration n) {
        regular_classes.add(n);
        currClass = n.f1.f0.tokenImage;
        classVarDecs.put(currClass,new Vector<VarDec>());
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
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
    public void visit(ClassExtendsDeclaration n) {
        extended_classes.add(n);
        currClass = n.f1.f0.tokenImage;
        classVarDecs.put(currClass,new Vector<VarDec>());
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
    }


    private String currClass;
    private String currType;
    /**
     * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public void visit(VarDeclaration n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        VarDec temp = new VarDec(currType, n.f1.f0.tokenImage);
        classVarDecs.get(currClass).add(temp);
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
    public void visit(MethodDeclaration n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
        n.f8.accept(this);
        n.f9.accept(this);
        n.f10.accept(this);
        n.f11.accept(this);
        n.f12.accept(this);
    }

    /**
     * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
    public void visit(FormalParameterList n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> Type()
    * f1 -> Identifier()
    */
    public void visit(FormalParameter n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> ","
    * f1 -> FormalParameter()
    */
    public void visit(FormalParameterRest n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    public void visit(Type n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    public void visit(ArrayType n) {
        currType = n.f0.tokenImage;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> "boolean"
    */
    public void visit(BooleanType n) {
        currType = n.f0.tokenImage;
        n.f0.accept(this);
    }

    /**
     * f0 -> "int"
    */
    public void visit(IntegerType n) {
        currType = n.f0.tokenImage;
        n.f0.accept(this);
    }

    /**
     * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
    public void visit(Statement n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
    public void visit(Block n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public void visit(AssignmentStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
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
    public void visit(ArrayAssignmentStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
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
    public void visit(IfStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
    }

    /**
     * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public void visit(WhileStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    /**
     * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public void visit(PrintStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    /**
     * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
    public void visit(Expression n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
    public void visit(AndExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public void visit(CompareExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public void visit(PlusExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public void visit(MinusExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public void visit(TimesExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public void visit(ArrayLookup n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public void visit(ArrayLength n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public void visit(MessageSend n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
    }

    /**
     * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
    public void visit(ExpressionList n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> ","
    * f1 -> Expression()
    */
    public void visit(ExpressionRest n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

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
    public void visit(PrimaryExpression n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public void visit(IntegerLiteral n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "true"
    */
    public void visit(TrueLiteral n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "false"
    */
    public void visit(FalseLiteral n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> <IDENTIFIER>
    */
    public void visit(Identifier n) {
        currType = n.f0.tokenImage;
        n.f0.accept(this);
    }

    /**
     * f0 -> "this"
    */
    public void visit(ThisExpression n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public void visit(ArrayAllocationExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    /**
     * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public void visit(AllocationExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
    }

    /**
     * f0 -> "!"
    * f1 -> Expression()
    */
    public void visit(NotExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
    }

    /**
     * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public void visit(BracketExpression n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }
}