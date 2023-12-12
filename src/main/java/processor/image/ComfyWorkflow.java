package main.java.processor.image;

import java.util.Optional;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
	private Map<String, String> paramsToNodeClassTypeMap = Map.ofEntries(
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
	private Map<String, String> semanticArgsToFieldsMap = Map.ofEntries(
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

	public ComfyWorkflow(ComfyWorkflowBuilder builder) {

		String jsonString = "";
		try {
			jsonString = new String(Files.readAllBytes(Paths.get(builder.baseWorkflowFile)));
		} catch (IOException e) {
			LOG.error("error reading workflow file");
			e.printStackTrace();
		}

		json = new JSONObject(jsonString);

		// making a copy so that outputDirectory is also included.
		Map<String, String> params = new HashMap<>(builder.params);
		params.put("outputPath", builder.outputDirectory);
		applyParams(params);
	}

	private void applyParams(Map<String, String> params) {
		// iteratively goes through params and modifies the relevant nodes
		// if those nodes don't exist, then they're ignored.
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();

			String classNameByKey = paramsToNodeClassTypeMap.get(key);
			String value = entry.getValue();

			Optional<String> classTypeOptional = json.keySet().stream()
					.filter(node -> json.getJSONObject(node).has("class_type")
							&& json.getJSONObject(node).getString("class_type").equals(classNameByKey))
					.findFirst();
			
			classTypeOptional.ifPresent(node -> {
				json.getJSONObject(node).getJSONObject("inputs").put(semanticArgsToFieldsMap.get(key), value);
			});
		}
	}

	@NoArgsConstructor
	public static class ComfyWorkflowBuilder {

		private String baseWorkflowFile;

		private Map<String, String> params;

		private String outputDirectory;

		public ComfyWorkflowBuilder setBaseWorkflowFile(String baseWorkflowFile) {
			this.baseWorkflowFile = baseWorkflowFile;
			return this;
		}

		public ComfyWorkflowBuilder setParams(Map<String, String> params) {
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