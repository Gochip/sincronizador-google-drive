package reader;
import java.io.*;
import java.util.*;
public class SincgdReader{
    public static final File configFile = new File("sincgd.config");
    private Properties prop;
    public SincgdReader(){
        prop = new Properties();
    }

    public void init() throws IOException, FileNotFoundException{
        FileInputStream fis = new FileInputStream(configFile);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        prop.load(isr);
    }

    public String getProperty(String key){
        return prop.getProperty(key);
    }
}
