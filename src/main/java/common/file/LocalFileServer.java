package main.java.common.file;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.processor.DbAndFileClient;


public class LocalFileServer implements FileServer {

    private static final Logger LOG = LoggerFactory.getLogger(DbAndFileClient.class);

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
            LOG.info("succesfully copied file: {}", destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File downloadFile(String name) {
        return new File(localDirectory + name);
    }

    @Override
    public void deleteFile(String name) {
        File file = new File(localDirectory + name);
        FileUtils.deleteQuietly(file);
    }

}
