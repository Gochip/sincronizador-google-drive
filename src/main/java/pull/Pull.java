package pull;

import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ChildReference;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;
import java.io.InputStream;
import model.*;

public class Pull{
    private Drive drive;
    private String directoryToSave;
    public Pull(Drive drive, String directoryToSave){
        this.drive = drive;
        this.directoryToSave = directoryToSave;
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
        String q = "(" + generateQ(directories) + ") and trashed=false";
        List<File> files = getFilesByFolderId(folderId, q);
        List<SincgdFile> filesSincgd = new LinkedList<>();
        for(File f : files){
            SincgdFile file = new SincgdFile(f);
            filesSincgd.add(file);
        }
        return filesSincgd;
    }
    
    /**
    * Returns a list of files given a folder id and filter q.
    * @param String folderId is the folder id.
    * @param String q is a filter.
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

    /**
    * Download the files of a folder.
    */
    public boolean downloadFolder(File file){
        return downloadFolder(file, new ManagerDownloaderFolder());
    }
    
    private boolean downloadFolder(File file, ManagerDownloaderFolder manager){
        System.out.println("Entering to folder: " + file.getTitle());
        manager.createDirectory(file.getTitle());
        boolean ok = true;
        List<File> files = getFilesByFolderId(file.getId(), "");
        for(File f : files){
            if(f.getMimeType().equals(Constants.MIME_TYPE_FOLDER)){
                ok &= downloadFolder(f, manager);
            }else{
                ok &= downloadFile(f);
            }
        }
        return ok;
    }

    /**
    * Download a file.
    * @param the file to download.
    */
    public boolean downloadFile(File file){
        try{
            System.out.println("Downloading: " + file.getTitle());

            InputStream is = drive.files().get(file.getId()).executeMediaAsInputStream();
            if(is != null){
                int b = 0;
                while((b = is.read()) != -1){
                    System.out.print(b);
                }
            }else{
                System.out.println("Input stream nulo");
            }
        } catch (IOException e) {
            // An error occurred.
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private class ManagerDownloaderFolder{
        String currentFolder;
        void createDirectory(String name){
            try{
                System.out.println("Creando PATH: " + directoryToSave + java.io.File.separator + name);
                Path path = FileSystems.getDefault().getPath(directoryToSave + "/" + name);
                
                java.nio.file.Files.createDirectory(path);
            }catch(java.nio.file.InvalidPathException e){
                
            }catch(IOException ex){
                
            }
        }

        void saveFile(File file){
            
        }
    }
}
