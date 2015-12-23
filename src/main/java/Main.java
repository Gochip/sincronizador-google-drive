import java.io.*;
import reader.*;

public class Main{
    public static void main(String args[]){
        SincgdReader reader = new SincgdReader();
        try{
            reader.init();
            String prop = reader.getProperty("a");
            System.out.println(prop);
        }catch(FileNotFoundException fnfex){
            System.err.println(fnfex.getMessage());
        }catch(IOException ioex){
            System.err.println(ioex.getMessage());
        }
    }
}
