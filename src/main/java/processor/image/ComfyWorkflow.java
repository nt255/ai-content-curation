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
	private Map<String, String> paramsToNodeClassTypeMap = Map.of(
			"width", "EmptyLatentImage", 
			"height", "EmptyLatentImage",
			"checkpoint", "CheckpointLoaderSimple", 
			"prompt", "CLIPTextEncode",
			"output_path", "Image Save",
			"kSteps", "KSamplerAdvanced",
			"kCFG", "KSamplerAdvanced"
			);
	private Map<String, String> semanticArgsToFieldsMap = Map.of(
			"width", "width",
			"height", "height",
			"prompt", "text",
			"checkpoint", "ckpt_name",
			"output_path", "output_path",
			"kSteps", "steps",
			"kCFG", "cfg"
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
		params.put("output_path", builder.outputDirectory);

		generateNewSeed();
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

	public void generateNewSeed() {
		Random random = new Random();
		BigInteger maxSeed = new BigInteger("18446744073709551614");
		BigInteger seed = new BigInteger(maxSeed.bitLength(), random);
		while (seed.compareTo(maxSeed) >= 0)
			seed = new BigInteger(maxSeed.bitLength(), random);

		json.getJSONObject("2").getJSONObject("inputs").put("noise_seed", seed);
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