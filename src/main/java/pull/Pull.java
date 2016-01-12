package pull;

import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.Drive.Files;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ChildReference;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import model.*;
import database.*;

public class Pull{
    private Drive drive;
    /**
    * Is the directory to save files synchronized.
    */
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
        manager.enterDirectory(file.getTitle());
        manager.createDirectory();
        boolean ok = true;
        List<File> files = getFilesByFolderId(file.getId(), "");
        for(File f : files){
            if(f.getMimeType().equals(Constants.MIME_TYPE_FOLDER)){
                ok &= downloadFolder(f, manager);
            }else{
                if(!manager.existsFile(f)){
                    ok &= downloadFile(f, manager);
                }
            }
        }
        manager.skipDirectory();
        return ok;
    }

    /**
    * Download a file.
    * @param the file to download.
    */
    public boolean downloadFile(File file, ManagerDownloaderFolder manager){
        try{
            InputStream is = drive.files().get(file.getId()).executeMediaAsInputStream();
            manager.saveFile(is, file);
        } catch (IOException e) {
            // An error occurred.
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private class ManagerDownloaderFolder{
        String currentDirectory = "";
        String FILE_SEPARATOR = java.io.File.separator;

        void createDirectory(){
            try{
                java.io.File file = new java.io.File(directoryToSave + currentDirectory);
                if(!file.exists()){
                    System.out.println("Creating directory: " + directoryToSave + currentDirectory);
                    Path path = FileSystems.getDefault().getPath(directoryToSave + currentDirectory);
                    java.nio.file.Files.createDirectory(path);
                    System.out.println("Directory created");
                }
            }catch(java.nio.file.InvalidPathException e){
                
            }catch(IOException ex){
                
            }
        }

        /**
        * Checks if exists file.
        */
        boolean existsFile(File file){
            /*try{
                String checksum = file.getMd5Checksum();
                if(checksum == null) return false;
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                java.io.File f = new java.io.File(directoryToSave + currentDirectory + FILE_SEPARATOR + file.getTitle());
                if(!f.exists()){
                    return false;
                }
                java.io.FileInputStream fis = new java.io.FileInputStream(f);
                byte buffer[] = new byte[1024];
                while(fis.read(buffer) != -1){
                    messageDigest.update(buffer);
                }
                byte[] hashBytes = messageDigest.digest();
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < hashBytes.length; i++) {
                    if ((0xff & hashBytes[i]) < 0x10) {
                        hexString.append("0"
                                + Integer.toHexString((0xFF & hashBytes[i])));
                    } else {
                        hexString.append(Integer.toHexString(0xFF & hashBytes[i]));
                    }
                }
                System.out.println(hexString + " ========= " + checksum);
                fis.close();
                return hexString.equals(checksum);
            } catch(NoSuchAlgorithmException algex){
                System.err.println(algex.getMessage());
            } catch(IOException ex){
                System.err.println(ex.getMessage());
            }
            return false;*/
            return false;
        }

        void enterDirectory(String name){
            this.currentDirectory += FILE_SEPARATOR + name;
        }

        void skipDirectory(){
            int index = this.currentDirectory.lastIndexOf(FILE_SEPARATOR);
            if(index != -1){
                this.currentDirectory = this.currentDirectory.substring(0, index);
            }else{
                this.currentDirectory = "";
            }
        }

        void saveFile(InputStream is, File file) throws IOException{
            String path = directoryToSave + FILE_SEPARATOR + currentDirectory + FILE_SEPARATOR + file.getTitle();
            System.out.println("Saving file: " + path);
            FileOutputStream os = new FileOutputStream(new java.io.File(path));
            if(is != null){
                int b = 0;
                while((b = is.read()) != -1){
                    os.write(b);
                }
            }else{
                System.out.println("Input stream nulo");
            }
            os.close();
            Database database = Database.getInstance();
            database.registerFile(file);
            System.out.println("File saved");
        }
    }
}
