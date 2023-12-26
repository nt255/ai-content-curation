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

    private final String outputDirectory;
    private Set<String> files;

    @Inject
    public ComfyFileManager(Properties properties) {
        this.outputDirectory = properties.getProperty("comfy.output.directory");
        this.files = getCurrentFiles();
    }

    private Set<String> getCurrentFiles() {
        try (Stream<Path> stream = Files.list(Paths.get(outputDirectory))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(s -> outputDirectory + s)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            LOG.error("comfy output directory not found!");
            e.printStackTrace();
        }
        return Set.of();
    }

    /**
     * Returns new files added since last checking output directory.
     * Output directory is checked, i.e. existing files kept track of
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
    
    public Set<String> waitForGeneratedFiles() {
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

        return generatedFiles;
    }

    public void clearDirectory() {
        File directory = new File(outputDirectory);
        try {
            FileUtils.cleanDirectory(directory);
            LOG.info("cleared output directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        files = Set.of();
    }
}
