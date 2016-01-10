package pull;

import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.FileList;
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

    private String generateQ(ArrayList<String> directories){
        int i = 0;
        StringBuilder q = new StringBuilder();
        for(i = 0; i < directories.size() - 1; i++){
            String d = directories.get(i);
            q.append("title='").append(d).append("'").append(" or ");
        }
        q.append("title='").append(directories.get(i)).append("'");
        return q.toString();
    }

    public List<SincgdFile> getFiles(ArrayList<String> directories){
        String folderId = "root";
        String q = generateQ(directories);
        List<File> files = getFilesByFolderId(folderId, q);
        List<SincgdFile> filesSincgd = new LinkedList<>();
        for(File f : files){
            SincgdFile file = new SincgdFile(f.getTitle());
            filesSincgd.add(file);
        }
        return filesSincgd;
    }
    
    /**
    * Returns a list of files given a folder id.
    * @param String folderId is the folder id.
    */
    private List<File> getFilesByFolderId(String folderId, String q){
        List<File> files = new LinkedList<>();
        try{
            Children.List request = drive.children().list(folderId);
            request = request.setQ(q);
            do {
                try {
                    ChildList children = request.execute();

                    for (ChildReference child : children.getItems()) {
                        File file = drive.files().get(child.getId()).execute();
                        if(!file.getExplicitlyTrashed()){
                            files.add(file);
                            System.out.println("File Mime Type: " + file.getTitle());
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
