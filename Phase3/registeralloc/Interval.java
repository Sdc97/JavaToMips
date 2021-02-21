package registeralloc;

public class Interval {
    private String var;
    private int start;
    private int end;
    public Registers register;
    public String location;
	public int offset;

    public Interval(String v, int s, int e){
        var = v;
        start = s;
        end = e;
    }
    public Interval(String v, int s) {
        var = v;
        start = s;
    }

    public void setEnd(int e) {
        end = e;
    }

    public String Var(){
        return var;
    }
    public int Start(){
        return start;
    }

    public int End(){
        return end;
    }

    public boolean hasRegister() {
        if (register == null) {
            return false;
        } else {
            return true;
        }
    }
    
}
