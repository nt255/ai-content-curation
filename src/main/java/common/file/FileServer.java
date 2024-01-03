package main.java.common.file;

import java.io.File;
import java.io.FileNotFoundException;

public interface FileServer {

    /**
     * Uploads file from sourceAbsolutePath to server as filename.
     */
    public void uploadFile(String filename, String sourceAbsolutePath);

    /**
     * Downloads filename from server and copies it over to 
     * targetDirectory/name.
     */
    public File downloadFile(String filename, String targetDirectory) 
            throws FileNotFoundException;

    /**
     * Deletes filename from server.
     */
    public void deleteFile(String filename);

}
