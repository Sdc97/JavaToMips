import java.util.*;

public class VTableCreator {
    static List<String> toporder;
    public static String CreateVTables() {
        String result = "";
        ClassTopSort();
        Map<String,List<List<String>>> vtables = new HashMap<>();
        for(String item: toporder) {
            if(item.equals(ContextType.mainClass)) continue;
            // parent -> ((A,run), (B,runB))
            // C has runB
            // C -> ((A,run), (C,runB))
            String parent = ContextType.class_parents.get(item);
            if(parent.equals("")) { // In this loop classes that have no parent
                List<List<String>> curr = new Vector<>();
                vtables.put(item, curr);
                
                for(String methodname : ContextType.class_to_methods.get(item).keySet()) {
                    List<String> tmp = new Vector<>();
                    tmp.add(item);
                    tmp.add(methodname);
                    curr.add(tmp);
                }
            } else {
                List<List<String>> parentvtable = vtables.get(parent);
                List<List<String>> curr = new Vector<>();
                vtables.put(item,curr);
                for(List<String> methodpair : parentvtable) {
                    if(!ContextType.class_to_methods.get(item).containsKey(methodpair.get(1))) {
                        curr.add(methodpair);
                    }
                }

                for(String methodname : ContextType.class_to_methods.get(item).keySet()) {
                    List<String> tmp = new Vector<>();
                    tmp.add(item);
                    tmp.add(methodname);
                    curr.add(tmp);
                }
            }
        }
        System.out.println(vtables);
        return result;
    }

    public static void ClassTopSort() {
        Set<String> visited = new HashSet<>();
        List<String> ordering = new Vector<>();
        for(String tmp: ContextType.class_parents.keySet()) {
            if(!visited.contains(tmp))
                TopSortRecursive(tmp, visited, ordering);
        }

        //System.out.println(ordering);
        toporder = ordering;
    }

    public static void TopSortRecursive(String current, Set<String> visited, List<String> ordering) {
        if(current.equals("")) return;
        visited.add(current);
        String parent = ContextType.class_parents.get(current);
        if(!visited.contains(parent))
            TopSortRecursive(parent, visited, ordering);
        
        ordering.add(current);
   }
}
