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
            String directory = reader.getProperty("directory");
            File file = new File(directory);
            if(file.exists() && file.isDirectory()){
                Drive drive = Authorization.getDriveService();
            
                Pull pull = new Pull(drive);
                List<SincgdFile> files = pull.getFiles("root");
                
                for(int i = 0; i < files.size(); i++){
                    System.out.println(files.get(i).getName());
                }
            }else{
                System.err.println("Error, el directorio " + directory + " no existe");
            }
        }catch(FileNotFoundException fnfex){
            System.err.println(fnfex.getMessage());
        }catch(IOException ioex){
            System.err.println(ioex.getMessage());
        }
    }
}
