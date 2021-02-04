import java.util.*;

public class VTableCreator {
    static List<String> toporder;
    public static String CreateVTables() {
        String result = "";
        ClassTopSort();
        Map<String,String> vtables = new HashMap<>();
        for(String item: toporder) {
            
        }
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
