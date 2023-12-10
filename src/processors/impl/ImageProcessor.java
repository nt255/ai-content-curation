package processors.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import processors.Processor;
import processors.clients.ComfyClient;
import processors.models.ComfyConfigs;
import processors.models.JobRequest;
import processors.models.JobResponse;

public class ImageProcessor implements Processor {
    
    private static final Logger LOG = LoggerFactory.getLogger(ImageProcessor.class);
    
    @Inject private ComfyClient comfyClient;

    @Override
    public JobResponse doWork(JobRequest request) {
        
        try {
            
            Map<String, String> map = Map.of(
                    "prompt", request.getPrompt(),
                    "height", request.getHeight().toString(),
                    "checkpoint", request.getCheckpoint());

            
            ComfyConfigs configs = new ComfyConfigs(map);
            comfyClient.applyConfigs(configs);
            comfyClient.switchWorkflow(request.getWorkflow());
            comfyClient.queuePrompt(1);

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
