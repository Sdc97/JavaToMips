import java.util.*;

public class TypeEnvironment {
    
    private Map<String,String> cont;
    private String name;
    private TypeEnvironment parent;

    public TypeEnvironment(String n) {
        cont = new HashMap<String,String>();
        name = n;
        parent = null;
    }

    public TypeEnvironment(String n, TypeEnvironment m) {
        cont = new HashMap<String,String>();
        name = n;
        parent = m;
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
        if(parent.contains(id)) {
            return parent.getType(id);
        } else {
            return getType(id);
        }
    }
}
