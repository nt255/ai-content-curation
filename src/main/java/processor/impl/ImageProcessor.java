package main.java.processor.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.processor.Processor;
import main.java.processor.image.ComfyClient;
import main.java.processor.image.ComfyFileManager;
import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResult;

public class ImageProcessor implements Processor {
    
    private static final Logger LOG = LoggerFactory.getLogger(ImageProcessor.class);
        
    @Inject private ComfyClient comfyClient;
    @Inject private ComfyFileManager comfyFileManager;
    

    @Override
    public JobResult doWork(JobRequest request) {
        
        try {
            Map<String, String> params = Map.of(
                    "prompt", request.getPrompt(),
                    "height", request.getHeight().toString(),
                    "width", request.getWidth().toString(),
                    "checkpoint", request.getCheckpoint(),
            		"kSteps", request.getKsteps().toString(),
            		"kCFG", request.getKcfg().toString()
            		);
            
            comfyClient.loadWorkflow(request.getWorkflow(), params);
            comfyClient.queuePrompt();

        } catch (IllegalStateException e) {
            LOG.error(e.getMessage());
        }
        
        // TODO: see if there is a better way to get files
        Set<String> generatedFiles = waitForGeneratedFiles();
        
        JobResult jobResponse = JobResult.builder()
                .id(request.getId())
                .isSuccessful(true)
                .errors(List.of())
                .build();

        return jobResponse;
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

}
