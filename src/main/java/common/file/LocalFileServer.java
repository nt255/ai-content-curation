package main.java.common.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Mock local only representation of a file server that just copies and deletes 
 * files from a specified directory.
 * 
 * @deprecated use {@link S3FileServer S3FileServer.class} instead
 */
@Deprecated
public class LocalFileServer implements FileServer {

    private static final Logger LOG = 
            LoggerFactory.getLogger(LocalFileServer.class);

    private final String localDirectory;

    @Inject
    public LocalFileServer(Properties properties) {
        localDirectory = properties.getProperty("local.output.directory");
    }

    @Override
    public void uploadFile(String name, String sourcePath) {
        // not really uploading anything
        // just writing to main directory
        File toCopy = new File(sourcePath);

        String destination = localDirectory + name;   
        File newFile = new File(destination);
        try {
            FileUtils.copyFile(toCopy, newFile);
            LOG.info("succesfully copied file to {}", destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File downloadFile(String name, String targetPath) throws FileNotFoundException {
        File file = new File(localDirectory + name);
        if (!file.exists())
            throw new FileNotFoundException();
        
        if (localDirectory.equals(targetPath))
            return file;    // no need to copy anything

        File copied = new File(targetPath + name);
        try {
            FileUtils.copyFile(file, copied);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copied;
    }

    @Override
    public void deleteFile(String name) {
        File file = new File(localDirectory + name);
        FileUtils.deleteQuietly(file);
    }

}
