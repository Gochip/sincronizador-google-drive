package database;

import java.io.RandomAccessFile;
import java.io.IOException;
import persistencia.*;

public class RegisterDatabase implements Grabable{
    private static final int FILE_ID_LENGTH = 30;
    private static final int FILE_NAME_LENGTH = 60;
    private static final int LONG_LENGTH = 8;

    private long time;
    private String fileId;
    private String fileName;

    public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return this.time;
    }

    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public String getFileId(){
        return this.fileId;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return this.fileName;
    }

    @Override
    public int sizeOf(){
        return FILE_ID_LENGTH + FILE_NAME_LENGTH + LONG_LENGTH;
    }

    @Override
    public void grabar(RandomAccessFile randomAccessFile){
        try {
            RegisterFile.writeString (randomAccessFile, fileId, FILE_ID_LENGTH);
            RegisterFile.writeString (randomAccessFile, fileName, FILE_NAME_LENGTH);
            randomAccessFile.writeLong(time);
       } catch(IOException e) {
            System.err.println("Error al escribir el registro: " + e.getMessage());
            System.exit(1);
       }
    }

    @Override
    public void leer( RandomAccessFile a ){
        try {
            this.fileId = RegisterFile.readString(a, FILE_ID_LENGTH).trim();
            this.fileName = RegisterFile.readString(a, FILE_NAME_LENGTH).trim();
            this.time = a.readLong();
       } catch(IOException e) {
            System.err.println("Error al leer el registro: " + e.getMessage());
            System.exit(1);
       }
    }

    @Override
    public int compareTo(Object other){
        RegisterDatabase register = (RegisterDatabase) other;
        return this.fileId.compareTo(register.fileId);
    }
}
