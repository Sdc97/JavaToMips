package registeralloc;
import java.util.*;

public class Algorithm{
    private List<Interval> active;
    private List<Interval> j_list;

    private int offset = 0;

    private int R = 17;
    private Set<Registers> available = new TreeSet<>(new regCompare());

    public void LinearScanRegisterAllocation(){
        initRegPool();
        active = new ArrayList<>();
        // Sort by increasing start point
        j_list.sort(Comparator.comparingInt(Interval::Start));
        for(Interval i: j_list) // For each live interval
        {
            //printAvailRegs();
            expireOldInterval(i);
            if(active.size() == R) // length(active) = R?
            {
                spillAtInterval(i);
            }
            else{
                i.register = getAvailableRegister();
                active.add(i);
            }
        }
    }

    private void expireOldInterval(Interval interval){

        //sort by increasing the end of interval
        active.sort(Comparator.comparingInt(Interval::End));
        for (Iterator<Interval> _interator = active.iterator(); _interator.hasNext(); ) {
            Interval j = _interator.next();
            if (j.End() >= interval.Start())
                return;

            _interator.remove();
            addRegisterBack(j.register);
        }
    }

    private void spillAtInterval(Interval interval){
        active.sort(Comparator.comparing(Interval::End));
        Interval spill = active.get(active.size()-1);
        
        if(spill.End() > interval.End()) {
            interval.register = spill.register;
            spill.location = getNewStackLoc(spill);
            spill.register = null;
            active.remove(spill);
            active.add(interval);
        } else {
            interval.location = getNewStackLoc(interval);
        }

        /*

        if(!active.isEmpty()) 
        {
            int index =active.size() - 1;
            spill = active.get(index--);
            while(index >= 0){
            spill = active.get(index--);
            }
            if(index < 0){
                spill = null;
            }
            else {spill = spill;}
        }
        if(!spill.equals(null) && spill.End() > interval.End())
        {
            _register.put(interval.Var(), _register.get(spill.Var()));
            _register.remove(spill.Var());
            _stack.add(spill.Var());
            active.remove(spill);
            active.add(interval);
            active.sort(Comparator.comparing(Interval::End));
        }else{
            _stack.add(interval.Var());
        }
        */
    }

    private String getNewStackLoc(Interval spill) {
        spill.offset = offset;
        offset++;

        return "local";
    }

    public void assignJList(List<Interval> jls) {
        j_list = jls;
    }

    private Registers getAvailableRegister() {
        Iterator<Registers> i = available.iterator();
        Registers tmp = i.next();
        available.remove(tmp);
        return tmp;
    }

    private void addRegisterBack(Registers r) {
        available.add(r);
    }

    private void initRegPool() {
        for(int i = 0; i < 8; i++) {
            Registers tmp = new Registers("$s" + i, "s");
            available.add(tmp);
        }

        for(int j = 0; j < 9; j++){
            Registers tmp = new Registers("$t" + j, "t");
            available.add(tmp);
        }
    }

    private void printAvailRegs() {
        System.out.print("Available regs: ");
        for(Registers i : available) {
            System.out.print(i.getReg() + " ");
        }
        System.out.println();
    }

}

class regCompare implements Comparator<Registers>{

    @Override
    public int compare(Registers o1, Registers o2) {
        return o1.getReg().compareTo(o2.getReg());
    }

}