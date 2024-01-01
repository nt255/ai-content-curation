package main.java.common.file;

import java.io.File;
import java.io.FileNotFoundException;

public interface FileServer {

    public void uploadFile(String name, String sourcePath);

    /**
     * Downloads file with given name from source
     * and copies it over to targetPath/name.
     */
    public File downloadFile(String name, String targetPath) 
            throws FileNotFoundException;

    public void deleteFile(String name);

}
