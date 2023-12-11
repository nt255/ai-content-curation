package main.java.processor.comfy;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import main.java.common.clients.HttpClient;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ComfyClient {

	@Inject
	private HttpClient httpClient;
	private Gson gson = new Gson();
	// this would likely be implemented as a Map<String, Loader> in the future.
	private WorkflowLoader defaultWorkflow = new WorkflowLoader("default");
	private WorkflowLoader fitnessAestheticsLoader;
	private WorkflowLoader dailyAffirmationsLoader;
	private WorkflowLoader upscalerLoader;
	private WorkflowLoader currentWorkflowLoader;
	private static final Logger LOG = LoggerFactory.getLogger(ComfyClient.class);

	@Inject
	public ComfyClient(@Named("fitnessAesthetics") WorkflowLoader fitnessAestheticsLoader,
			@Named("dailyAffirmations") WorkflowLoader dailyAffirmationsLoader,
			@Named("upscaler") WorkflowLoader upscalerLoader) {
		this.dailyAffirmationsLoader = dailyAffirmationsLoader;
		this.fitnessAestheticsLoader = fitnessAestheticsLoader;
		this.upscalerLoader = upscalerLoader;

		// default, for now.
		this.currentWorkflowLoader = fitnessAestheticsLoader;
	}

	// currently a switch statement, but would work with a MapOfLoaders if we use
	// one as a private field.
	public void switchWorkflow(String workflowName) {
		switch (workflowName) {
		case "fitnessAesthetics":
			this.currentWorkflowLoader = fitnessAestheticsLoader;
			break;
		case "dailyAffirmations":
			this.currentWorkflowLoader = dailyAffirmationsLoader;
			break;
		default:
			LOG.error("Unable to load workflow: " + workflowName);
			LOG.error("Setting workflow to default.");
			this.currentWorkflowLoader = defaultWorkflow;
		}
	}

	public void applyConfigs(ComfyConfigs configs) {
		this.currentWorkflowLoader.applyConfigs(configs);
	}

	/**
	 * NOTE The methods below will NOT return anything. They will make a call to the
	 * localhost that hosts your instance of ComfyUI, and the output images will be
	 * saved to a directory, which by default is [comfyUi's
	 * drive]://comfyclient_output/
	 **/

	// will load in default options when queued without arguments
	// currently simply prints out the error, but should be updated to update
	// relevant JobResponse
	public void queuePrompt(int generationCount) throws IllegalStateException {
		LOG.info("A prompt was queued with no arguments. Proceeding...");
		LOG.info("Verifying that workflow has been loaded...");
		if (currentWorkflowLoader.getWorkflow() != null) {
			LOG.info(String.format("Workflow %s has been verified.", currentWorkflowLoader.getWorkflowName()));
			LOG.info("Deleting history.");
			clearQueueHistory();
			// useful for debug LOG.info(currentWorkflowLoader.toString());
			LOG.info(String.format("Generating %s outputs...", generationCount));
			for (int i = 1; i <= generationCount; i++) {
				sendTask(currentWorkflowLoader.getWorkflow());
			}
		} else {
			LOG.error("Error! Workflow is null!");
			throw new IllegalStateException(
					"Workflow has not been loaded correctly (it is null)! Check to see that workflow names are properly spelled.");
		}
	}
	
	public void upscaleImages(String[] imageIds) {
		LOG.info("");
	}

	private void sendTask(ComfyWorkflow workflow) {
		try {
			currentWorkflowLoader.setRandomSeed();
			Map<String, Object> payloadMap = new HashMap<>();
			payloadMap.put("prompt", workflow.getWorkflow());
			String jsonPayload = gson.toJson(payloadMap);
			JSONObject jsonObject = new JSONObject(jsonPayload);

			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			headers.put("charset", "utf-8");
			String comfyPromptUrl = "http://127.0.0.1:8188/prompt";
			String comfyHistoryUrl = "http://127.0.0.1:8188/history";
			String responseHistory = httpClient.makeRequest(HttpClient.RequestMethod.GET, comfyHistoryUrl, headers, jsonObject);
			
			HttpClient httpClient = new HttpClient();
			String response = httpClient.makeRequest(HttpClient.RequestMethod.POST, comfyPromptUrl, headers, jsonObject);

			System.out.println("Response from server: " + response);
			System.out.println(responseHistory);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearQueueHistory() {
		LOG.info("Sent request to clear history.");

		String comfyHistoryUrl = "http://127.0.0.1:8188/history";
		Map<String, Boolean> options = new HashMap<String, Boolean>();
		options.put("clear", true);
		String jsonPayload = gson.toJson(options);
		JSONObject jsonObject = new JSONObject(jsonPayload);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("charset", "utf-8");

		httpClient.makeRequest(HttpClient.RequestMethod.POST, comfyHistoryUrl, headers, jsonObject);
	}
}
