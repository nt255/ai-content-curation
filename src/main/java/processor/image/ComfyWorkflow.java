package main.java.processor.image;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import main.java.common.models.ImageParams;

@Getter
public class ComfyWorkflow {

    private static final Logger LOG = LoggerFactory.getLogger(ComfyWorkflow.class);
    
    private JSONObject json;

    public ComfyWorkflow(ComfyWorkflowBuilder builder) {

        String jsonString = "";
        try {
            jsonString = new String(
                    Files.readAllBytes(Paths.get(builder.baseWorkflowFile)));
        } catch (IOException e) {
            LOG.error("error reading workflow file");
            e.printStackTrace();
        }

        json = new JSONObject(jsonString);
        generateNewSeed();
        
        ImageParams params = builder.params;

        if (params.getHeight() != null) {
            json.getJSONObject("6").getJSONObject("inputs")
            .put("height", params.getHeight());
        }

        if (params.getWidth() != null) {
            json.getJSONObject("6").getJSONObject("inputs")
            .put("width", params.getWidth());
        }

        if (params.getCheckpoint() != null) {
            json.getJSONObject("1").getJSONObject("inputs")
            .put("ckpt_name", params.getCheckpoint());
        }

        if (params.getPrompt() != null) {
            json.getJSONObject("9").getJSONObject("inputs")
            .put("text", params.getPrompt());
        }
        
        if (builder.outputDirectory != null) {
            json.getJSONObject("14").getJSONObject("inputs")
            .put("output_path", builder.outputDirectory);
        }
    }

    public void generateNewSeed() {
        Random random = new Random();
        BigInteger maxSeed = new BigInteger("18446744073709551614");
        BigInteger seed = new BigInteger(maxSeed.bitLength(), random);
        while (seed.compareTo(maxSeed) >= 0)
            seed = new BigInteger(maxSeed.bitLength(), random);

        json.getJSONObject("2").getJSONObject("inputs")
        .put("noise_seed", seed);
    }


    @NoArgsConstructor
    public static class ComfyWorkflowBuilder {

        private String baseWorkflowFile;

        private ImageParams params;
        
        private String outputDirectory;

        public ComfyWorkflowBuilder setBaseWorkflowFile(String baseWorkflowFile) {
            this.baseWorkflowFile = baseWorkflowFile;
            return this;
        }

        public ComfyWorkflowBuilder setParams(ImageParams params) {
            this.params = params;
            return this;
        }
        
        public ComfyWorkflowBuilder setOutputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public ComfyWorkflow build() {
            return new ComfyWorkflow(this);
        }
    }

}