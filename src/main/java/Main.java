import java.io.*;
import java.util.*;

import reader.*;
import model.*;
import pull.*;

public class Main{
    public static void main(String args[]){
        SincgdReader reader = new SincgdReader();
        try{
            reader.init();
            
            Pull pull = new Pull();
            List<SincgdFile> files = pull.getFiles("");
            
            for(int i = 0; i < files.size(); i++){
                System.out.println(files.get(i).getName());
            }
            
        }catch(FileNotFoundException fnfex){
            System.err.println(fnfex.getMessage());
        }catch(IOException ioex){
            System.err.println(ioex.getMessage());
        }
    }
}
