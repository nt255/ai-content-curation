package main.java.common.file;

import java.io.File;

public interface FileServer {
    
    public void uploadFile(String name, String sourcePath);
    
    public File downloadFile(String name);
    
    public void deleteFile(String name);

}
