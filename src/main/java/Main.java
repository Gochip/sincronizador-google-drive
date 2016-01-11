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
            String directoryToSave = reader.getProperty("directory");
            String accepted = reader.getProperty("accepted");
            File file = new File(directoryToSave);
            if(file.exists() && file.isDirectory()){
                String[] d = accepted.split(",");
                ArrayList<String> acceptedArray = new ArrayList<>(Arrays.asList(d));
                Drive drive = Authorization.getDriveService();

                Pull pull = new Pull(drive, directoryToSave);
                List<SincgdFile> files = pull.getFiles(acceptedArray);

                for(int i = 0; i < files.size(); i++){
                    SincgdFile f = files.get(i);
                    pull.downloadFolder(f.getFile());
                }
            }else{
                System.err.println("Error, el directorio " + directoryToSave + " no existe");
            }
        }catch(FileNotFoundException fnfex){
            System.err.println(fnfex.getMessage());
        }catch(IOException ioex){
            System.err.println(ioex.getMessage());
        }
    }
}
