package pull;

import java.util.*;
import model.*;

public class Pull{
    public Pull(){
        
    }

    public List<SincgdFile> getFiles(String directory){
        List<SincgdFile> files = new LinkedList<>();
        SincgdFile file = new SincgdFile("a");
        files.add(file);
        return files;
    }
}
