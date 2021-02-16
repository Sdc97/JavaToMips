import java.util.*;

public class VTableCreator {
    static List<String> toporder;
    public static String CreateVTables() {
        String result = "";
        ClassTopSort();
        Map<String,List<List<String>>> vtables = new HashMap<>();
        for(String item: toporder) {
            if(item.equals(ContextType.mainClass)) continue;
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
        
        for(Map.Entry<String,List<List<String>>> entry: vtables.entrySet()) {
            String classname = entry.getKey();
            int offsetcount = 0;
            System.out.println("const vmt_" + classname);
            Map<String,Integer> tempmethod = new HashMap<>();
            ContextType.classMethodOffsets.put(classname, tempmethod);
            for(List<String> item : entry.getValue()) {
                System.out.println("   :" + item.get(0) + "." + item.get(1));
                tempmethod.put(item.get(1), offsetcount);
                offsetcount += 4;
            }
            System.out.println();
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

    public static void varClassOffsetMapping() {
       for(String item: toporder) {
            if(item.equals(ContextType.mainClass)) continue;
            int varoffset = 4;
            String parent = ContextType.class_parents.get(item);
            Map<String,Integer> tempvar = new HashMap<>();
            ContextType.classVarOffsets.put(item, tempvar);
            if(parent.equals("")) {
                for(String s: ContextType.classVarField.get(item).keySet()) {
                    tempvar.put(s,varoffset);
                    varoffset += 4;
                }
            } 
            else {
                Map<String,Integer> parentvars = ContextType.classVarOffsets.get(parent);
                for(String s: ContextType.classVarField.get(item).keySet()) {
                    tempvar.put(s,varoffset);
                    varoffset += 4;
                }
                for(String s: parentvars.keySet()) {
                    if(!tempvar.containsKey(s)) {
                        tempvar.put(s, parentvars.get(s));
                        varoffset += 4;
                    }
                }
            }
        }
    }
}
