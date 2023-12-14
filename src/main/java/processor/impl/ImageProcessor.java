package main.java.processor.impl;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.ImageDao;
import main.java.common.db.models.ImageDbModel;
import main.java.common.file.FileServer;
import main.java.common.models.ImageParams;
import main.java.common.models.JobState;
import main.java.processor.Processor;
import main.java.processor.image.ComfyClient;
import main.java.processor.image.ComfyFileManager;
import main.java.processor.models.ProcessorResult;
import zmq.util.function.Optional;

public class ImageProcessor implements Processor<ImageParams> {

    private static final Logger LOG = LoggerFactory.getLogger(ImageProcessor.class);

    @Inject private ImageDao imageDao;
    @Inject private FileServer fileServer;

    @Inject private ComfyClient comfyClient;
    @Inject private ComfyFileManager comfyFileManager;


    @Override
    public ProcessorResult process(UUID id, ImageParams params) {

        try {
            comfyClient.loadWorkflow(params);
            comfyClient.queuePrompt();

        } catch (IllegalStateException e) {
            LOG.error(e.getMessage());
        }
        Set<String> generatedFiles = waitForGeneratedFiles();
        assert(generatedFiles.size() == 1);     // temporary for now
        
        String localImagePath = generatedFiles.iterator().next();
        
        ProcessorResult result = ProcessorResult.builder()
                .id(id)
                .isSuccessful(true)
                .outputString(localImagePath)
                .errors(List.of())
                .build();

        return result;
    }


    private Set<String> waitForGeneratedFiles() {
        LOG.info("waiting for new files(s) to be generated..");
        LOG.info("polling every second");
        Set<String> generatedFiles;
        do {
            try {
                TimeUnit.SECONDS.sleep(1l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            generatedFiles = comfyFileManager.getNewFiles();
        } while (generatedFiles.isEmpty());

        return generatedFiles;
    }
    
	public BigInteger generateNewSeed() {
		Random random = new Random();
		BigInteger maxSeed = new BigInteger("18446744073709551614");
		BigInteger seed = new BigInteger(maxSeed.bitLength(), random);
		while (seed.compareTo(maxSeed) >= 0)
			seed = new BigInteger(maxSeed.bitLength(), random);
		return seed;
	}

    
    @Override
    public void save(UUID id, ProcessorResult result) {
        
        ImageDbModel existing = imageDao.get(id).get();
        existing.setLastModifiedOn(Instant.now());
        existing.setState(JobState.COMPLETED);

        Optional.ofNullable(result.getOutputString()).ifPresent(path -> {
            UUID imageId = UUID.randomUUID();
            String ext = FilenameUtils.getExtension(path);
            String newFilename = imageId + "." + ext;
            
            fileServer.uploadFile(newFilename, path);
            existing.setOutputFilename(newFilename);

            comfyFileManager.clearDirectory();
        });

        imageDao.delete(id);
        imageDao.insert(existing);
    }

}
