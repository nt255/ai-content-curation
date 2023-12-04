package processors.clients;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.clients.HttpClient;
import processors.ProcessorRouter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ComfyClient {
	
    @Inject private HttpClient httpClient;
    private Properties properties = new Properties();
    private static final Logger LOG = LoggerFactory.getLogger(ProcessorRouter.class);
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapNode(Map<String, Object> promptWorkflow, String key) {
        Object node = promptWorkflow.get(key);
        if (node instanceof Map) {
            return (Map<String, Object>) node;
        } else {
            throw new IllegalArgumentException("Node with key '" + key + "' is not a Map<String, Object>");
        }
    }
    
    // loads a workflow from local directory
    // ideally this would be fetched from a database of some sort later    
    private Map<String, Object> loadWorkflow(String workflowPrefix) throws IOException {
    	LOG.info(String.format("Loading %s_workflow.json", workflowPrefix));
    	File jsonFile = new File(String.format("src/processors/clients/%s_workflow.json", workflowPrefix));
    	ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> workflow = objectMapper.readValue(jsonFile, Map.class);
        return workflow;
    }
    
    // applies configurations to a given workflow.
    // currently only accepts checkpoint, height, width, and prompt.
    // need to refactor and see if there's an elegant way of dealing with this.
    @SuppressWarnings("unchecked")
	private void applyConfigs(Map<String, Object> workflow, Map<String, String> configs) {
    	LOG.info("Applying arguments to override default configs, if applicable..."); 
    	Map<String, Object> chkpointLoaderNode = getMapNode(workflow, "1");
         Map<String, Object> latentImageNode = getMapNode(workflow, "6");
         Map<String, Object> posNode = getMapNode(workflow, "9");
         // Map<String, Object> loraTwoNode = getMapNode(promptWorkflow, "7");
         // Map<String, Object> loraThreeNode = getMapNode(promptWorkflow, "8");
         // Map<String, Object> loraOneNode = getMapNode(promptWorkflow, "4");
         // Map<String, Object> negNode = getMapNode(promptWorkflow, "10");
         // Map<String, Object> ksamplerNode = getMapNode(promptWorkflow, "2");
         // Map<String, Object> saveImageNode = getMapNode(promptWorkflow, "14");
         Map<String, Object> checkpointInputs = (Map<String, Object>) chkpointLoaderNode.get("inputs");
         Map<String, Object> latentInputs = (Map<String, Object>) latentImageNode.get("inputs");
         Map<String, Object> positivePromptInputs = (Map<String, Object>) posNode.get("inputs");
         
         System.out.println("Here is the workflow before changes...");
         System.out.println(workflow);
         
         if (configs.containsKey("checkpoint")) {
        	 LOG.info("'checkpoint' found. overwriting default...");
        	 checkpointInputs.put("ckpt_name", configs.get("checkpoint"));
         } 
         
         if (configs.containsKey("height")) {
        	 LOG.info("'height' found. overwriting default...");
        	 latentInputs.put("height", Integer.parseInt(configs.get("height")));
         } 
         
         if (configs.containsKey("width")) {
        	 LOG.info("'width' found. overwriting default...");
        	 latentInputs.put("width", Integer.parseInt(configs.get("width")));
         } 
         
         if (configs.containsKey("prompt")) {
        	 LOG.info("'prompt' found. overwriting default...");
             positivePromptInputs.put("text", configs.get("prompt"));
         }
         
         System.out.println("Here is the workflow after any changes: ");
         System.out.println(workflow);
    }
    
	
    /**
     * NOTE
     *  The methods below will NOT return anything. They will make a call to the localhost that hosts your instance of ComfyUI,
     *  and the output images will be saved to a directory, which by default is [comfyUi's drive]://comfyclient_output/
     *  **/
    
	// will load in default options when queued without arguments
    // currently simply prints out the error, but should be updated to update relevant JobResponse
	public void queuePrompt() {
		LOG.info("A prompt was queued with no arguments. Proceeding...");
		Map<String, Object> workflow = new HashMap<>();
		try {
			workflow =  loadWorkflow("default");
        } catch (IOException e) {
            e.printStackTrace();
        }

        applyConfigs(workflow, Collections.emptyMap());
	}

	public void queuePrompt(String workflowPrefix, String[] args) {
		LOG.info("A prompt was queued with the following arguments: " + args);
		Map<String, Object> workflow = new HashMap<>();
		
		// overwrites defaults 
		try {
			workflow = loadWorkflow(workflowPrefix);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		
		// parsing arguments as key-value pairs. we can manage more settings if needed.
		Map<String, String> keyValuePairs = new HashMap<>();
		LOG.info("Parsing arguments...");
		for (String arg: args) {
			String[] keyValue = arg.split(":");
			
			if (keyValue.length == 2) {
				keyValuePairs.put(keyValue[0], keyValue[1].trim());
			} 
		}
		
		
		// access and print values via keys
		for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			LOG.debug("KEY (OPTION): " + key + ", VALUE: " + value);
		}
		
		applyConfigs(workflow, keyValuePairs);
		
	}

}
