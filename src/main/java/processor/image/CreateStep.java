package main.java.processor.image;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.models.image.ImageParams;
import main.java.processor.Step;
import main.java.processor.comfy.ComfyClient;
import main.java.processor.comfy.ComfyFileManager;

class CreateStep implements Step<ImageParams> {
    
    private static final Logger LOG = LoggerFactory.getLogger(CreateStep.class);
    
    private final ComfyClient comfyClient;
    private final ComfyFileManager comfyFileManager;

    @Inject
    public CreateStep(ComfyClient comfyClient, ComfyFileManager comfyFileManager) {
        this.comfyClient = comfyClient;
        this.comfyFileManager = comfyFileManager;
    }

    @Override
    public String execute(String previousOutput, ImageParams step) {
        if (!previousOutput.isEmpty())
            throw new IllegalStateException(
                    "CREATE cannot work on an existing image");
        
        try {
            comfyClient.loadWorkflow(step);
            comfyClient.queuePrompt();
        } catch (IllegalStateException e) {
            LOG.error(e.getMessage());
        }

        Set<String> generatedFiles = comfyFileManager.waitForGeneratedFiles();
        if (generatedFiles.size() > 1)
            throw new IllegalStateException(
                    "should not have generated more than one file");

        return generatedFiles.iterator().next();
    }

}
