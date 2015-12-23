import com.google.api.services.drive.Drive;

import java.io.*;
import java.util.*;

import authorization.*;
import model.*;
import pull.*;
import reader.*;

public class Main{
    public static void main(String args[]){
        SincgdReader reader = new SincgdReader();
        try{
            reader.init();
            
            Drive drive = Authorization.getDriveService();
            
            Pull pull = new Pull(drive);
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
