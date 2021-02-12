
import java.util.*;

public class Algorithm{
    private List<Interval> active;
    private List<Interval> j_list;
    private Registers reg_pool;
    private Map<String, Registers> _register;
    private Set<String> _stack;

    private void LinearScanRegisterAllocation(){
        //active{} ?!!
        for(Interval i:j_list)
        {
             expireOldInterval(i);
             if(i.Start() > 0)//possibly we need to get the R number of register?
             {
                 spillAtInterval(i);
             }
             else{
                 _register.put(i.Var(), reg_pool);
                 active.add(i);
             }
        }

    }

    private void expireOldInterval(Interval interval){

        //sort by increasing the end of interval
        active.sort(Comparator.comparingInt(Interval::End));
        for (Iterator<Interval> _interator = active.iterator(); _interator.hasNext(); ) {
            Interval i = _interator.next();
            if (i.End() >= interval.Start())
                return;

            _interator.remove();
            reg_pool.R_remove(_register.get(i.Var()));
            //need to add register[i] to pool of free register !?

        }
    }

    private void spillAtInterval(Interval interval){
        active.sort(Comparator.comparing(Interval::End));
        Interval spill = null;
        //for last interval in active!
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
        
    }

}