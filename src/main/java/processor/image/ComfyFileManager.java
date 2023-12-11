package main.java.processor.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ComfyFileManager {

    private static final Logger LOG = LoggerFactory.getLogger(ComfyFileManager.class);

    private String outputDirectory;
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
    public Set<String> getNewFiles() {
        Set<String> currentFiles = getCurrentFiles();
        Set<String> copy = Set.copyOf(currentFiles);
        currentFiles.removeAll(files);
        files = copy;

        LOG.info("found the following new files {}", currentFiles);
        return currentFiles;
    }

    public void clearDirectory(String name) {
        LOG.info("purging output directory");
        File directory = new File(outputDirectory);
        for (File f: directory.listFiles())
            f.delete();
        files = Set.of();
    }
}
