import java.util.HashMap;
import java.util.Map;

public class ContextType {

    // HashMap that maps a class name to its parent. If a class has no parent it will map to the empty string "".
    private static Map<String,String> class_parents = new HashMap<>();

    public String getTypeEnvType(String id) {
        return ""; // TODO: Implement type environments and retrieve the specified id val.
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
        while(!currtype.equals("")) { // run through parents of 
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
}
