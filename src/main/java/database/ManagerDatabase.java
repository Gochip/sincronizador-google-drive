package database;

import com.google.api.services.drive.model.*;
import com.google.api.client.util.DateTime;

import java.util.*;
import java.io.RandomAccessFile;
import java.io.IOException;
import persistencia.*;

/**
* This class represents the Database of the application.
* Allows save important data for proper operation.
*/
public class ManagerDatabase{
    private static ManagerDatabase me;
    private DirectAccessFile directAccessFile;

    private ManagerDatabase(){
        directAccessFile = new DirectAccessFile("sincgd.dat", "rw");
    }

    public static ManagerDatabase getInstance(){
        if(me == null){
            me = new ManagerDatabase();
        }
        return me;
    }

    public void registerFile(File file){
        DateTime dateTime = file.getModifiedDate();
        long t1 = dateTime.getValue();
        RegisterDatabase register = new RegisterDatabase();
        register.setFileId(file.getId());
        register.setFileName(file.getTitle());
        register.setTime(t1);
        System.out.println("FileId: " + register.getFileId());
        directAccessFile.add(register);
    }
    
    public boolean existsFile(File file){
        RegisterDatabase register = new RegisterDatabase();
        register.setFileId(file.getId());
        return directAccessFile.search(register) != -1;
    }
}
