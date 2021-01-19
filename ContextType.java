import java.util.*;

public class ContextType {

    public static String mainClass;


    public String currclass;
    public Map<String,String> methodField;
    public Map<String,String> methodArgField;
    public static Map<String,Map<String,String>> classVarField = new HashMap<>();

    // HashMap that maps a class name to its parent. If a class has no parent it will map to the empty string "".
    public static Map<String,String> class_parents = new HashMap<>();
    public static Map<String,Map<String,MethodDescriptor>> class_to_methods = new HashMap<>();

    public String getTypeEnvType(String id) {
        if(methodField.containsKey(id)) {
            return methodField.get(id);
        }
        if(methodArgField.equals(null)) { // Main class will hit this if it cannot find it in methodfield.
            throw new Error("Type error");
        }
        if(methodArgField.containsKey(id)) {
            return methodArgField.get(id);
        }

        String checkclass = currclass;
        Map<String,String> currmap;
        while(!checkclass.equals("")) { // run through fields(C)
            currmap = classVarField.get(checkclass);
            if(currmap.containsKey(id)) {
                return currmap.get(id);
            }
            checkclass = class_parents.get(checkclass);
        }

        throw new Error("Type error"); // Usage of undefined variable.
    }

    public MethodDescriptor methodtype(String classname, String methodname) {

        // If the primary map doesnt contain the classname throw error, or if submap doesnt contain method throw error.
        if(!class_to_methods.containsKey(classname) || !class_to_methods.get(classname).containsKey(methodname)) { 
            throw new Error("Type error");
        }

        return class_to_methods.get(classname).get(methodname); // Return the proper MethodDescriptor (Must be constructed elsewhere.)
    }

    /**
     * Returns true if t2 is a subtype of t1, false otherwise.
     * @param t1
     * @param t2
     * @return true/false
     */
    public boolean isSubType(String t1, String t2) { 
        if(t1.matches("int|boolean|int\\[\\]") || t2.matches("int|boolean|int\\[\\]")) { // If one of the types is a primitive, they must be the same.
            if(t1.equals(t2)) {
                return true;
            } else {
                return false;
            }
        }

        if(t1.equals(t2)) {
            return true;
        }

        String currtype = class_parents.get(t2); // Get parent class of t2, if it exists. Will be empty string if it does not exist.
        while(!currtype.equals("")) { // run through parents of t2, until match or cannot find t1.
            if(currtype.equals(t1)) {
                return true;
            }
            currtype = class_parents.get(currtype);
        }
        return false; 
    }

    public void addClassParent(String child, String parent) {
        class_parents.put(child, parent);
    }

    public void addClassMethodMap(String classname, Map<String,MethodDescriptor> methodmap) {
        class_to_methods.put(classname, methodmap);
    }

    // Check if all strings in a list are distinct
    public static boolean distinct(List<String> t) {
        for (int i = 0; i < t.size(); i++) {
            for (int j = i + 1; j < t.size(); j++) {
                if (t.get(i).equals(t.get(j)))
                    return false;
            }
        }
        return true;
    }
}