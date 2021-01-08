import java.util.*;

public class TypeEnvironment {
    
    private Map<String,String> cont;
    private String name;
    private TypeEnvironment parentClass;

    public TypeEnvironment(String n) {
        cont = new HashMap<String,String>();
        name = n;
        parentClass = null;
    }

    public TypeEnvironment(String n, TypeEnvironment m) {
        cont = new HashMap<String,String>();
        name = n;
        parentClass = m;
    }

    public String getName() {
        return name;
    }

    public void insertId(String id, String type) {
        cont.put(id, type);
    }

    public boolean contains(String id) {
        if(cont.containsKey(id)) {
            return true;
        } else {
            return false;
        }
    }

    public String getType(String id) {
        if(parentClass.contains(id)) {
            return parentClass.getType(id);
        } else {
            return getType(id);
        }
    }
}
