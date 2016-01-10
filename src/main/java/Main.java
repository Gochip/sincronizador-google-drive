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
            String accepted = reader.getProperty("accepted");
            File file = new File(directory);
            if(file.exists() && file.isDirectory()){
                String[] d = accepted.split(",");
                for(int i = 0; i < d.length; i++){
                    System.out.println(d[i]);
                }
                ArrayList<String> acceptedArray = new ArrayList<>(Arrays.asList(d));
                Drive drive = Authorization.getDriveService();

                Pull pull = new Pull(drive);
                List<SincgdFile> files = pull.getFiles(acceptedArray);

                for(int i = 0; i < files.size(); i++){
                    String name = files.get(i).getName();
                    if(acceptedArray.contains(name)){
                        System.out.println("OK");
                    }
                    System.out.println("File name: " + name);
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
