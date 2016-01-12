package database;

import com.google.api.services.drive.model.*;
import com.google.api.client.util.DateTime;

/**
* This class represents the Database of the application.
* Allows save important data for proper operation.
*/
public class Database{
    private static Database me;
    public static Database getInstance(){
        if(me == null){
            me = new Database();
        }
        return me;
    }

    public void registerFile(File file){
        DateTime dateTime = file.getModifiedDate();
        long t1 = dateTime.getValue();
        System.out.println(file.getTitle() + " ==========> " + t1);
    }
}
