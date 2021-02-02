import visitor.*;
import syntaxtree.*;

public class VaporPrintVisitor extends GJNoArguDepthFirst<String>{
    /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
    public String visit(Goal n) {
        String _ret = "";
        _ret += n.f1.accept(new VTableCreator());
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }
}

class VTableCreator extends GJNoArguDepthFirst<String> {
    /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   public String visit(TypeDeclaration n) {
    String _ret=null;
    n.f0.accept(this);
    return _ret;
 }
}
