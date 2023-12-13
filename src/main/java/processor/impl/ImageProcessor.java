package main.java.processor.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.processor.Processor;
import main.java.processor.image.ComfyClient;
import main.java.processor.image.ComfyFileManager;
import main.java.processor.models.ProcessorResponse;

public class ImageProcessor implements Processor {
    
    private static final Logger LOG = LoggerFactory.getLogger(ImageProcessor.class);
        
    @Inject private ComfyClient comfyClient;
    @Inject private ComfyFileManager comfyFileManager;
    

    @Override
    public ProcessorResponse doWork(UUID id, Map<String, String> params) {
        
        try {
            comfyClient.loadWorkflow(params.get("workflow"), params);
            comfyClient.queuePrompt();

        } catch (IllegalStateException e) {
            LOG.error(e.getMessage());
        }
        
        Set<String> generatedFiles = waitForGeneratedFiles();
        assert(generatedFiles.size() == 1);     // temporary for now
        
        ProcessorResponse jobResult = ProcessorResponse.builder()
                .id(id)
                .isSuccessful(true)
                .outputString(generatedFiles.iterator().next())
                .errors(List.of())
                .build();

        return jobResult;
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
