import visitor.GJDepthFirst;

import java.util.List;
import java.util.Vector;

import syntaxtree.*;

public class ExpressionVisitors extends GJDepthFirst<String,ContextType> {

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
    public String visit(Expression n, ContextType argu) {
      String _ret=n.f0.accept(this, argu);
      return _ret;
   }

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
            return "boolean";
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
        if(p1.equals("int[]") && p2.equals("int")) {
            return p2;
        }
        throw new Error("Type error");
     }
     
     /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
     public String visit(ArrayLength n, ContextType argu) {
        String ptype = n.f0.accept(this, argu);
        if(!ptype.equals("int[]")) {
            throw new Error("Type error");
        }
        return "int";
     }
    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public String visit(MessageSend n, ContextType argu) {
      ListVisitor arggetter = new ListVisitor(); // Declare ListVisitor to get the list of argument types.
      String p = n.f0.accept(this, argu); // Get type of primary expression.
      String id = n.f2.f0.tokenImage; // Get identifier of class method to call.
      MethodDescriptor m = argu.methodtype(p, id); // Get a MethodDescriptor object of the method specified. Throws error if it cannot find p or the method.
      List<String> argtypes = n.f4.accept(arggetter, argu); // Retrieve the list of args given

      if(m.argument_types.size() != argtypes.size()) { // If the sizes dont match then we obviously have a type error.
         throw new Error("Type error");
      }

      for(int i = 0; i < m.argument_types.size(); i++) { // Check the list of given against the list of types in the method, if any mismatch throw error.
         if(!argu.isSubType(m.argument_types.get(i), argtypes.get(i))) {
            throw new Error("Type error");
         }
      }
      return m.return_type;
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

     public String visit(PrimaryExpression n, ContextType argu) {
       
        String _ret = n.f0.accept(this,argu);
        return _ret;
     }
     
     /**
    * f0 -> <INTEGER_LITERAL>
    */
     public String visit(IntegerLiteral n, ContextType argu) {

        String _ret = "int";
        return _ret;
     }
     /**
    * f0 -> "true"
    */
     public String visit(TrueLiteral n, ContextType argu) {

        String _ret = "boolean";
        return _ret;
     }
     /**
    * f0 -> "false"
    */
     public String visit(FalseLiteral n, ContextType argu) {

        String _ret = "boolean";
        return _ret;
     }
     /**
    * f0 -> <IDENTIFIER>
    */
     public String visit(Identifier n, ContextType argu) {

        String _ret = argu.getTypeEnvType(n.f0.tokenImage); // Gets the type of the identifier, throws if not in domain.
        return _ret;
     }
     /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, ContextType argu) {
        String _ret = argu.currclass;
        if(argu.currclass.equals(ContextType.mainClass)) { // If the current class is the main class, we cannot use "this".
            throw new Error("Type error");
        }
        return _ret;
     }
     /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
     public String visit(ArrayAllocationExpression n, ContextType argu) {
        String Atype = n.f3.accept(this, argu);
        if(!Atype.equals("int")) {
            throw new Error("Type error");
        }
        return "int[]";
     }
     /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
     public String visit(AllocationExpression n, ContextType argu) {
         String id = n.f1.f0.tokenImage;
         if(!ContextType.class_parents.containsKey(id))
         {
            throw new Error("Type error");
         } 
         return n.f1.f0.tokenImage; // Simply returnin the type of the class we are creating.
     }
     /**
    * f0 -> "!"
    * f1 -> Expression()
    */
     public String visit(NotExpression n, ContextType argu) {
        String _ret = n.f1.accept(this, argu);
        if(!_ret.equals("boolean"))
        {
            throw new Error("Type error");
        }
        return _ret;
     }
     /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
     public String visit(BracketExpression n, ContextType argu) {
        return n.f1.accept(this, argu);
     }

}


class ListVisitor extends GJDepthFirst<List<String>,ContextType> {
      private List<String> temp = new Vector<String>(); // List of argument types, in order left to right.
      private ExpressionVisitors getCurrent = new ExpressionVisitors(); // Visitor for getting individual argument types, from the class above.

      public List<String> visit(NodeOptional n, ContextType argu) {
         if ( n.present() )
            return n.node.accept(this,argu);
         else
            return temp;
      }

   /**
    * f0 -> Expression()
    * f1 -> ( ExpressionRest() )*
    */
    public List<String> visit(ExpressionList n, ContextType argu) {
      String toAdd = n.f0.accept(getCurrent, argu);
      temp.add(toAdd);
      n.f1.accept(this, argu);
      return temp;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
    public List<String> visit(ExpressionRest n, ContextType argu) {
      String toAdd = n.f1.accept(getCurrent, argu);
      temp.add(toAdd);
      return null; // dont use the return value of these, since the object contains the list of argument types, in order.
   }
}