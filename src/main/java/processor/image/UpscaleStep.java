package main.java.processor.image;

import com.google.inject.Inject;

import main.java.common.models.image.ImageParams;
import main.java.processor.Step;
import main.java.processor.comfy.ComfyClient;
import main.java.processor.comfy.ComfyFileManager;

class UpscaleStep implements Step<ImageParams> {

    private final ComfyClient comfyClient;
    private final ComfyFileManager comfyFileManager;

    @Inject
    public UpscaleStep(ComfyClient comfyClient, ComfyFileManager comfyFileManager) {
        this.comfyClient = comfyClient;
        this.comfyFileManager = comfyFileManager;
    }

    @Override
    public String execute(String previousOutput, ImageParams step) {
        if (previousOutput.isEmpty())
            throw new IllegalStateException(
                    "UPSCALE must work on an existing image");

        step.setImagePath(previousOutput);
        comfyClient.loadUpscalerWorkflow(step);
        comfyClient.queuePrompt();

        return comfyFileManager.waitForGeneratedFile("upscale_0001.jpg");
    }
}
