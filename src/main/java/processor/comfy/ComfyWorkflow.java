package main.java.processor.comfy;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ComfyWorkflow {

    private static final Logger LOG = LoggerFactory.getLogger(ComfyWorkflow.class);

    private JSONObject json;

    public ComfyWorkflow(AlternativeWorkflowBuilder builder) {

        String jsonString = "";
        try {
            jsonString = new String(
                    Files.readAllBytes(Paths.get(builder.baseWorkflowFile)));
        } catch (IOException e) {
            LOG.error("error reading workflow file");
            e.printStackTrace();
        }

        json = new JSONObject(jsonString);

        Map<String, String> params = builder.params;

        if (params.containsKey("height")) {
            json.getJSONObject("6").getJSONObject("inputs")
            .put("height", params.get("height"));
        }

        if (params.containsKey("width")) {
            json.getJSONObject("6").getJSONObject("inputs")
            .put("width", params.get("width"));
        }

        if (params.containsKey("checkpoint")) {
            json.getJSONObject("1").getJSONObject("inputs")
            .put("ckpt_name", params.get("checkpoint"));
        }

        if (params.containsKey("prompt")) {
            json.getJSONObject("9").getJSONObject("inputs")
            .put("text", params.get("prompt"));
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
    public static class AlternativeWorkflowBuilder {

        private String baseWorkflowFile;

        private Map<String, String> params;

        public AlternativeWorkflowBuilder setBaseWorkflowFile(String baseWorkflowFile) {
            this.baseWorkflowFile = baseWorkflowFile;
            return this;
        }

        public AlternativeWorkflowBuilder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public ComfyWorkflow build() {
            return new ComfyWorkflow(this);
        }
    }

}