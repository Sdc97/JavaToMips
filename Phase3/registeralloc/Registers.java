package registeralloc;
public class Registers {
    //need to creat the registers in here
    private String register;
    private String type; // "t" or "s"

    public Registers(String reg, String t){
        register = reg;
        type = t;
    }

    public String getReg() {
      return register;
    }

    public String getType() {
      return type;
    }
}


