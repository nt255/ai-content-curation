package main.java.processor.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.processor.Processor;
import main.java.processor.comfy.ComfyClient;
import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResponse;

public class ImageProcessor implements Processor {
    
    private static final Logger LOG = LoggerFactory.getLogger(ImageProcessor.class);
    
    @Inject private ComfyClient comfyClient;

    @Override
    public JobResponse doWork(JobRequest request) {
        
        try {
            Map<String, String> params = Map.of(
                    "prompt", request.getPrompt(),
                    "height", request.getHeight().toString(),
                    "width", request.getWidth().toString(),
                    "checkpoint", request.getCheckpoint());
            
            comfyClient.loadWorkflow(request.getWorkflow(), params);
            comfyClient.queuePrompt();

        } catch (IllegalStateException e) {
            LOG.error("Could not queue prompt!");
            LOG.error(e.getMessage());
        } catch(Exception e) {
            LOG.error(e.getMessage());
        }
        
        JobResponse jobResponse = JobResponse.builder()
                .id(request.getId())
                .isSuccessful(true)
                .errors(List.of())
                .build();

        return jobResponse;
    }

}
