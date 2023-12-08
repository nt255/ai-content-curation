package processors.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Random;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.clients.HttpClient;
import processors.ProcessorRouter;
import processors.models.ComfyWorkflow;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ComfyClient {
	
    @Inject private HttpClient httpClient;
    private Gson gson = new Gson();
    // this would likely be implemented as a Map<String, Loader> in the future.
    private WorkflowLoader defaultWorkflow = new WorkflowLoader("default");
    private WorkflowLoader fitnessAestheticsLoader;
    private WorkflowLoader dailyAffirmationsLoader;
    private WorkflowLoader currentWorkflowLoader;
    private static final Logger LOG = LoggerFactory.getLogger(ProcessorRouter.class);
    
    @Inject
    public ComfyClient(@Named("fitnessAesthetics") WorkflowLoader fitnessAestheticsLoader, @Named("dailyAffirmations") WorkflowLoader dailyAffirmationsLoader) {
    	this.dailyAffirmationsLoader = dailyAffirmationsLoader;
    	this.fitnessAestheticsLoader = fitnessAestheticsLoader;
    	
    	// default, for now.
    	this.currentWorkflowLoader = fitnessAestheticsLoader;
    }
    
    // currently a switch statement, but would work with a MapOfLoaders if we use one as a private field.
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
    
    /**
     * NOTE
     *  The methods below will NOT return anything. They will make a call to the localhost that hosts your instance of ComfyUI,
     *  and the output images will be saved to a directory, which by default is [comfyUi's drive]://comfyclient_output/
     *  **/
    
	// will load in default options when queued without arguments
    // currently simply prints out the error, but should be updated to update relevant JobResponse
	public void queuePrompt() throws IllegalStateException {
		LOG.info("A prompt was queued with no arguments. Proceeding...");
		LOG.info("Verifying that workflow has been loaded...");
		if (currentWorkflowLoader.getWorkflow() != null) {
			LOG.info(String.format("Workflow %s has been verified, proceeding...", currentWorkflowLoader.getWorkflowName()));
			LOG.info(currentWorkflowLoader.toString());
			sendTask(currentWorkflowLoader.getWorkflow());
		} else {
			LOG.error("Error! Workflow is null!");
			throw new IllegalStateException("Workflow has not been loaded correctly (it is null)! Check to see that workflow names are properly spelled.");
		}
	}
    
    private void sendTask(ComfyWorkflow workflow) {
    	try {
    		Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("prompt", workflow.getWorkflow());
            String jsonPayload = gson.toJson(payloadMap);
            byte[] jsonBytes = jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
            LOG.info(jsonPayload); 
            
            String url = "http://127.0.0.1:8188/prompt";

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("charset", "utf-8");

            // Make POST request using HttpClient
            HttpClient httpClient = new HttpClient();
            String response = httpClient.makeRequest(HttpClient.RequestMethod.POST, url, headers, jsonBytes);

            
            // Process response or handle as needed
            System.out.println("Response from server: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//	public void queuePrompt(String workflowPrefix, String[] args) {
//		LOG.info("A prompt was queued with the following arguments: " + args);
//		Map<String, Object> workflow = new HashMap<>();
//		
//		try {
//			workflow = loadWorkflow(workflowPrefix);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//				
//		
//		// parsing arguments as key-value pairs. we can manage more settings if needed.
//		Map<String, String> keyValuePairs = new HashMap<>();
//		LOG.info("Parsing arguments...");
//		for (String arg: args) {
//			String[] keyValue = arg.split(":");
//			
//			if (keyValue.length == 2) {
//				keyValuePairs.put(keyValue[0], keyValue[1].trim());
//			} 
//		}
//		
//		
//		// access and print values via keys
//		for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			
//			LOG.debug("KEY (OPTION): " + key + ", VALUE: " + value);
//		}
//		
//		applyConfigs(workflow, keyValuePairs);
//		sendTask(workflow);
//	}

}
