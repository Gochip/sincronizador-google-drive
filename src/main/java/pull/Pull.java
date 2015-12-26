package pull;

import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;

import java.util.*;
import java.io.IOException;
import model.*;

public class Pull{
    private Drive drive;
    public Pull(Drive drive){
        this.drive = drive;
    }
    
    public String toFolderId(String directory){
        return directory;
    }

    public List<SincgdFile> getFiles(String directory){
        String folderId = toFolderId(directory);
        List<File> files = getFilesByFolderId(folderId);
        List<SincgdFile> filesSincgd = new LinkedList<>();
        for(File f : files){
            SincgdFile file = new SincgdFile(f.getTitle());
            filesSincgd.add(file);
        }
        return filesSincgd;
    }
    
    /**
    * Returns a list of folder id given a folder id.
    */
    private List<File> getFilesByFolderId(String folderId){
        List<File> files = new LinkedList<>();
        try{
            Children.List request = drive.children().list(folderId);
            do {
                try {
                    ChildList children = request.execute();

                    for (ChildReference child : children.getItems()) {
                        File file = drive.files().get(child.getId()).execute();
                        if(!file.getExplicitlyTrashed()){
                            files.add(file);
                            System.out.println("File Id: " + file.getTitle());
                        }
                    }
                    request.setPageToken(children.getNextPageToken());
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                    request.setPageToken(null);
                }
            } while (request.getPageToken() != null &&
                     request.getPageToken().length() > 0);
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }       
        return files;
    }
}
