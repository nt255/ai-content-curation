package processors.clients;

import processors.ProcessorRouter;
import processors.models.ComfyConfigs;
import processors.models.ComfyNode;
import processors.models.ComfyWorkflow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class WorkflowLoader {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessorRouter.class);
	private String workflowName;
	private ComfyWorkflow loadedWorkflow;

	@Inject
	public WorkflowLoader(@Named("workflowName") String workflowName) {
		this.workflowName = workflowName;
		this.loadedWorkflow = loadWorkflowFromPath();
	}

	private String generateWorkflowPath() {
		return String.format("src/processors/clients/%s_workflow.json", this.workflowName);
	}

	@SuppressWarnings("unchecked")
	private ComfyNode getMapNode(ComfyWorkflow workflow, String key) {
		Map<String, Object> workflowMap = workflow.getWorkflow();
		Object node = workflowMap.get(key);
		if (node instanceof Map<?, ?>) {
			Map<String, Object> mapNode = (Map<String, Object>) node;
			return new ComfyNode(mapNode);
		}
		return null;
	}

	public ComfyWorkflow loadWorkflowFromPath() {
		try {
			String workflowPath = generateWorkflowPath();
			LOG.info(String.format("Loading workflow from path '%s'", workflowPath));
			String content = new String(Files.readAllBytes(Paths.get(workflowPath)));
			LOG.info("Successfully loaded.");
			LOG.info(content);
            JSONObject json = new JSONObject(content);
			LOG.info(json.toString());
			ComfyWorkflow comfyWorkflow = parseWorkflow(json);
			return comfyWorkflow;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			LOG.info("Unable to fetch workflow. Leaving reference as null.");
			return null;
		}
	}
	
	private ComfyWorkflow parseWorkflow(JSONObject json) {
		try {
            Map<String, Object> workflowMap = new HashMap<>();

            for (String key : json.keySet()) {
                JSONObject workflowObj = json.getJSONObject(key);

                Map<String, Object> elementData = new HashMap<>();
                JSONObject inputsObj = workflowObj.getJSONObject("inputs");
                elementData.put("inputs", inputsObj.toMap());

                elementData.put("class_type", workflowObj.getString("class_type"));

                workflowMap.put(key, elementData);
            }
            return new ComfyWorkflow(workflowMap);
        } catch (JSONException e) {
            LOG.error("Error parsing workflow data: " + e.getMessage());
            return null;
        }
	}

	@SuppressWarnings("unchecked")
	public void applyConfigs(ComfyConfigs configs) {
		LOG.info("Applying arguments to override default configs, if applicable...");
		ComfyNode chkpointLoaderNode = getMapNode(this.loadedWorkflow, "1");
		ComfyNode latentImageNode = getMapNode(this.loadedWorkflow, "6");
		ComfyNode posNode = getMapNode(this.loadedWorkflow, "9");
		ComfyNode ksamplerNode = getMapNode(this.loadedWorkflow, "2");
		// this next part COULD use another POJO, but that might be excessive.
		Map<String, Object> ksamplerInputs = (Map<String, Object>) ksamplerNode.get("inputs");
		Map<String, Object> checkpointInputs = (Map<String, Object>) chkpointLoaderNode.get("inputs");
		Map<String, Object> latentInputs = (Map<String, Object>) latentImageNode.get("inputs");
		Map<String, Object> positivePromptInputs = (Map<String, Object>) posNode.get("inputs");

		LOG.info("DEBUG: Here is the workflow before changes...");
		System.out.println(this.loadedWorkflow);
		LOG.info("DEBUG: and the arguments...");
		System.out.println(configs);

		if (configs.containsKey("checkpoint")) {
			LOG.info("'checkpoint' found. overwriting default...");
			checkpointInputs.put("ckpt_name", configs.get("checkpoint"));
		}

		if (configs.containsKey("height")) {
			LOG.info("'height' found. overwriting default...");
			latentInputs.put("height", Integer.parseInt((String) configs.get("height")));
		}

		if (configs.containsKey("width")) {
			LOG.info("'width' found. overwriting default...");
			latentInputs.put("width", Integer.parseInt((String) configs.get("width")));
		}

		if (configs.containsKey("prompt")) {
			LOG.info("'prompt' found. overwriting default...");
			positivePromptInputs.put("text", configs.get("prompt"));
		}

		LOG.info("And here it is after the changes:");
		System.out.println(this.loadedWorkflow);
	}

	public String getWorkflowName() {
		return this.workflowName;
	}
	
	public ComfyWorkflow getWorkflow() {
		return this.loadedWorkflow;
	}
}