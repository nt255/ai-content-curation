package main.java.processor.image;

import java.util.Optional;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import main.java.common.models.BaseParams;
import main.java.common.models.image.ImageParams;

@Getter
class ComfyWorkflow {

	private static final Logger LOG = LoggerFactory.getLogger(ComfyWorkflow.class);

	private final JSONObject json;
	private final Map<String, String> paramsToNodeClassTypeMap = Map.ofEntries(
			Map.entry("width", "EmptyLatentImage"), 
			Map.entry("height", "EmptyLatentImage"),
			Map.entry("checkpoint", "CheckpointLoaderSimple"), 
			Map.entry("prompt", "CLIPTextEncode"),
			Map.entry("outputPath", "Image Save"),
			Map.entry("kSteps", "KSamplerAdvanced"),
			Map.entry("kCFG", "KSamplerAdvanced"),
			Map.entry("kNoise", "KSamplerAdvanced"),
			Map.entry("upscaleNoise", "UltimateSDUpscale"),
			Map.entry("upscaleSteps", "UltimateSDUpscale"),
			Map.entry("upscaleCFG", "UltimateSDUpscale"),
			Map.entry("imagePath", "LoadImage")
			);
	private final Map<String, String> semanticArgsToFieldsMap = Map.ofEntries(
			Map.entry("width", "width"),
			Map.entry("height", "height"),
			Map.entry("prompt", "text"),
			Map.entry("checkpoint", "ckpt_name"),
			Map.entry("outputPath", "output_path"),
			Map.entry("kSteps", "steps"),
			Map.entry("kCFG", "cfg"),
			Map.entry("kNoise", "noise_seed"),
			Map.entry("upscaleNoise", "noise_seed"),
			Map.entry("upscaleSteps", "steps"),
			Map.entry("upscaleCFG", "cfg"),
			Map.entry("imagePath", "image")
			);

	private ComfyWorkflow(ComfyWorkflowBuilder builder) {

		String jsonString = "";
		try {
			jsonString = new String(Files.readAllBytes(Paths.get(builder.baseWorkflowFile)));
		} catch (IOException e) {
			LOG.error("error reading workflow file");
			e.printStackTrace();
		}
		
        json = new JSONObject(jsonString);
        generateNewSeed();
        
        // sets output directory
        if (builder.outputDirectory != null) {
        	String outputDirectory = builder.outputDirectory;
        	for (String key : json.keySet()) {
        		JSONObject node = json.getJSONObject(key);
        		if (node.has("inputs")) {
        			JSONObject inputs = node.getJSONObject("inputs");
        			if (inputs.has("output_path")) {
        				inputs.put("output_path", outputDirectory);
        			}
        		}
        	}
        }
        
        ImageParams params = builder.params;

        Field[] imageParamsFields = ImageParams.class.getDeclaredFields();
        Field[] baseParamsFields = BaseParams.class.getDeclaredFields();
        
        List<Field> allFields = new ArrayList<>(Arrays.asList(imageParamsFields));
        allFields.addAll(Arrays.asList(baseParamsFields));
        
        // looks through all relevant fields and updates their values
        for (Field field : allFields) {
        	field.setAccessible(true);
        
        	try {
        		Object value = field.get(params);
        		if (value != null) {
        			String key = field.getName();
        			String classTypeByKey = paramsToNodeClassTypeMap.get(key);
        			
        			Optional<String> classTypeOptional = json.keySet().stream()
        					.filter(node -> json.getJSONObject(node).has("class_type")
        							&& json.getJSONObject(node).getString("class_type").equals(classTypeByKey))
        					.findFirst();
        			
        			classTypeOptional.ifPresent(node -> {
        				json.getJSONObject(node).getJSONObject("inputs").put(semanticArgsToFieldsMap.get(key), value);
        			});
        		}
        	} catch (IllegalAccessException e) {
        		LOG.error("Unable to access fields of params.");
        		e.printStackTrace();
        	}
		}
	}

	@NoArgsConstructor
	static class ComfyWorkflowBuilder {

		private String baseWorkflowFile;

		private ImageParams params;
        
        private String outputDirectory;

		ComfyWorkflowBuilder setBaseWorkflowFile(String baseWorkflowFile) {
			this.baseWorkflowFile = baseWorkflowFile;
			return this;
		}

        ComfyWorkflowBuilder setParams(ImageParams params) {
            this.params = params;
            return this;
        }
        
        ComfyWorkflowBuilder setOutputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

		ComfyWorkflow build() {
			return new ComfyWorkflow(this);
		}
	}
	
	// generates a new seed in the appropriate node for the private field json.
	// used for generating plain images
	// for other workflows, the seed in the .json file is already set to 0 and should not be touched!
	private void generateNewSeed() {
		Random random = new Random();
		BigInteger maxSeed = new BigInteger("18446744073709551614");
		BigInteger seed = new BigInteger(maxSeed.bitLength(), random);
		while (seed.compareTo(maxSeed) >= 0)
			seed = new BigInteger(maxSeed.bitLength(), random);
		for (String key : json.keySet()) {
	        JSONObject innerObj = json.optJSONObject(key);
	        if (innerObj != null && innerObj.has("inputs") && innerObj.has("class_type")) {
	            JSONObject inputs = innerObj.getJSONObject("inputs");
	            if (inputs.has("noise_seed")) {
	            	LOG.info("successfully set seed");
	                inputs.put("noise_seed", seed); 
	            }
	        }
	    }
	}
}