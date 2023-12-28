package main.java.processor.comfy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ComfyFileManager {

    private static final Logger LOG = LoggerFactory.getLogger(ComfyFileManager.class);

    private final String workingDirectory;
    private Set<String> files;

    @Inject
    public ComfyFileManager(Properties properties) {
        this.workingDirectory = properties.getProperty("comfy.working.directory");
        this.files = getCurrentFiles();
    }

    private Set<String> getCurrentFiles() {
        try (Stream<Path> stream = Files.list(Paths.get(workingDirectory))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(s -> workingDirectory + s)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            LOG.error("comfy working directory not found!");
            e.printStackTrace();
        }
        return Set.of();
    }

    /**
     * Returns new files added since last checking working directory.
     * Working directory is checked, i.e. existing files kept track of
     * when constructing and when calling this method.
     * 
     * @return new files
     */
    private Set<String> getNewFiles() {
        Set<String> currentFiles = getCurrentFiles();
        Set<String> copy = Set.copyOf(currentFiles);
        currentFiles.removeAll(files);
        files = copy;

        LOG.info("found the following new files {}", currentFiles);
        return currentFiles;
    }

    public String waitForGeneratedFile() {
        LOG.info("waiting for new files(s) to be generated..");
        LOG.info("polling every second");

        Set<String> generatedFiles;
        do {
            try {
                TimeUnit.SECONDS.sleep(1l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            generatedFiles = getNewFiles();
        } while (generatedFiles.isEmpty());
        
        if (generatedFiles.size() > 1)
            throw new IllegalStateException(
                    "should not have generated more than one file");

        return generatedFiles.iterator().next();
    }

    public void clearDirectory() {
        File directory = new File(workingDirectory);
        try {
            FileUtils.cleanDirectory(directory);
            LOG.info("cleared working directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        files = Set.of();
    }
}
