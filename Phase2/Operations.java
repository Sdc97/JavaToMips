import java.util.*;

public class Operations {

    public static String Add(String left, String right)
    {
        return "Add(" + left + " " + right + ")";   //Add(a b)
    }

    public static String Sub(String left, String right)
    {
        return "Sub(" + left + " " + right + ")";   //Sub(a b)
    }

    public static String MulS(String left, String right)
    {
        return "MultS(" + left + " " + right + ")";   //MultS(a b)
    }

    public static String Eq(String left, String right)
    {
        return "Eq(" + left + " " + right + ")";   //Eq(a b)
    }

    public static String Lt(String left, String right)
    {
        return "Lt(" + left + " " + right + ")";   //Lt(a b)
    }

    public static String LtS(String left, String right)
    {
        return "LtS(" + left + " " + right + ")";   //LtS(a b)
    }

    public static String PrintIntS(String output)
    {
        return "PrintIntS(" + output + ")";   //PrintIntS(t.2)
    }

    public static String HeapAllocZ(int n)
    {
        return "HeapAllocZ(" + n + ")";   //HeapAllocZ(n)
    }

    public static String Error()
    {
        return "Error( \"null pointer\" )";
    }





}
