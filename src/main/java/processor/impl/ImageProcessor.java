package main.java.processor.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
        
    	 BigInteger noise = generateNewSeed();
    	
    	 LOG.error(params.toString());
    	// creates base image
        try {
            Map<String, String> configurationParams = Map.of(
            		"kNoise", noise.toString(),
                    "prompt", params.get("prompt"),
                    "height", params.get("height"),
                    "width", params.get("width"),
                    "checkpoint", params.get("checkpoint"),
            		"kSteps", "25",
            		"kCFG", "5"
            		);
            
            comfyClient.loadWorkflow(params.get("workflow"), configurationParams);
            comfyClient.queuePrompt();

        } catch (IllegalStateException e) {
            LOG.error(e.getMessage());
        }
        Set<String> generatedFiles = waitForGeneratedFiles();
        assert(generatedFiles.size() == 1);     // temporary for now
        
        String localImagePath = generatedFiles.iterator().next();
        
        /*
         * BETA
         * This is just to demonstrate and test that the workflow loading for our 
         * intended basic workflows is operational.
         */
        
        // takes image from previous flow and upscales it 2x
//        try {
//        	Map<String, String> params = Map.of(
//        			"upscaleNoise", noise.toString(),
//        			"prompt", request.getPrompt(),
//        			"height", request.getHeight().toString(),
//        			"width", request.getWidth().toString(),
//        			"checkpoint", request.getCheckpoint(),
//        			"upscaleSteps", "25",
//        			"upscaleCFG", "7",
//        			"imagePath", localImagePath
//        			);
//        	comfyClient.loadWorkflow("upscaler", params);
//        	comfyClient.queuePrompt();
//        } catch (IllegalStateException e) {
//        	LOG.error(e.getMessage());
//        }
//        Set<String> generatedUpscaleFiles = waitForGeneratedFiles();
//        assert(generatedUpscaleFiles.size() == 1);
//        
//        String localUpscaledImagePath = generatedUpscaleFiles.iterator().next();
        
        ProcessorResponse jobResult = ProcessorResponse.builder()
                .id(id)
                .isSuccessful(true)
                .outputString(localImagePath)
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
    
	public BigInteger generateNewSeed() {
		Random random = new Random();
		BigInteger maxSeed = new BigInteger("18446744073709551614");
		BigInteger seed = new BigInteger(maxSeed.bitLength(), random);
		while (seed.compareTo(maxSeed) >= 0)
			seed = new BigInteger(maxSeed.bitLength(), random);
		return seed;
	}
}
