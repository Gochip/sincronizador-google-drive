package model;

import com.google.api.services.drive.model.*;

public class SincgdFile{
    private File file;
    public SincgdFile(File file){
        this.file = file;
    }
    
    public File getFile(){
        return this.file;
    }

    public String getName(){
        return this.file.getTitle();
    }
    
    public String getId(){
        return "";
    }
}
