package main.java.common.file;

import java.io.File;
import java.io.FileNotFoundException;

public interface FileServer {
    
    public void uploadFile(String name, String sourcePath);
    
    public File downloadFile(String name) throws FileNotFoundException;
    
    public void deleteFile(String name);

}
