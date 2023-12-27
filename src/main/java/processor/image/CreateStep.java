package main.java.processor.image;

import com.google.inject.Inject;

import main.java.common.models.image.ImageParams;
import main.java.processor.Step;
import main.java.processor.comfy.ComfyClient;
import main.java.processor.comfy.ComfyFileManager;

class CreateStep implements Step<ImageParams> {
    
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
        
        comfyClient.loadWorkflow(step);
        comfyClient.queuePrompt();

        return comfyFileManager.waitForGeneratedFile();
    }

}
