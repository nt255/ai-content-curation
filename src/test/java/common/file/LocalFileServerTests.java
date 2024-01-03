package test.java.common.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import main.java.common.file.LocalFileServer;
import test.java.TestWithInjections;

@SuppressWarnings("deprecation")
public class LocalFileServerTests extends TestWithInjections {
    
    private static final String TEMP_TXT = "temp.txt";

    @Inject private LocalFileServer fileServer;

    @Test
    void allMethodsTest() {
        String tempDirectory = System.getProperty("java.io.tmpdir");
        File tempFile = getTempFile(tempDirectory);
        String tempFilePath = tempFile.getParentFile().getAbsolutePath() + "\\";
        String tempFileAbsolutePath = tempFile.getAbsolutePath();
        
        String newFilename = UUID.randomUUID().toString() + ".txt";
        
        assertThrows(
                FileNotFoundException.class,
                () -> fileServer.downloadFile(
                        newFilename, tempFilePath));   
        
        fileServer.uploadFile(newFilename, tempFileAbsolutePath);
        
        File downloaded = null;
        try {
            downloaded = fileServer.downloadFile(newFilename, tempFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        assertEquals(tempFilePath + newFilename, 
                downloaded.getAbsolutePath());
        
        fileServer.deleteFile(newFilename);
        assertThrows(
                FileNotFoundException.class,
                () -> fileServer.downloadFile(
                        newFilename, tempFilePath));
        
        // clean-up temp directory
        deleteFile(tempFilePath + TEMP_TXT);
        deleteFile(tempFilePath + newFilename);
    }

    private File getTempFile(String tempDirectory) {
        String s = UUID.randomUUID().toString();
        File file = new File(tempDirectory + TEMP_TXT);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(s.getBytes());
            stream.flush();
            stream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    
    private void deleteFile(String absolutePath) {
        File file = new File(absolutePath);
        FileUtils.deleteQuietly(file);
    }

}
