package processors.clients;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.Properties;
import java.util.Random;

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
         Map<String, Object> ksamplerNode = getMapNode(workflow, "2");
         // Map<String, Object> loraTwoNode = getMapNode(promptWorkflow, "7");
         // Map<String, Object> loraThreeNode = getMapNode(promptWorkflow, "8");
         // Map<String, Object> loraOneNode = getMapNode(promptWorkflow, "4");
         // Map<String, Object> negNode = getMapNode(promptWorkflow, "10");
         // Map<String, Object> ksamplerNode = getMapNode(promptWorkflow, "2");
         // Map<String, Object> saveImageNode = getMapNode(promptWorkflow, "14";
         // setting random seed for generation
         Map<String, Object> ksamplerInputs = (Map<String, Object>) ksamplerNode.get("inputs");
         Random random = new Random();
         BigInteger min = BigInteger.ONE;
         BigInteger max = new BigInteger("18446744073709551614");
         BigInteger randomBigInt = new BigInteger(max.bitLength(), random);
         while (randomBigInt.compareTo(min) < 0 || randomBigInt.compareTo(max) >= 0) {
             randomBigInt = new BigInteger(max.bitLength(), random);
         }
         ksamplerInputs.put("noise_seed", randomBigInt);

         System.out.println("Random number: " + randomBigInt);
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
    
    private void sendTask(Map<String, Object> workflow) {
    	try {
    		ObjectMapper objectMapper = new ObjectMapper();
    		Map<String, Map<String, Object>> payloadMap = new HashMap<>();
            payloadMap.put("prompt", workflow);
            String jsonPayload = objectMapper.writeValueAsString(payloadMap);
            URL url = new URL("http://127.0.0.1:8188/prompt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            byte[] taskData = jsonPayload.getBytes(StandardCharsets.UTF_8);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", String.valueOf(taskData.length));

            connection.setDoOutput(true);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(taskData);
            } catch (Exception e) {
            	e.printStackTrace();
            }

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Message: " + responseMessage);

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
        sendTask(workflow);
	}

	public void queuePrompt(String workflowPrefix, String[] args) {
		LOG.info("A prompt was queued with the following arguments: " + args);
		Map<String, Object> workflow = new HashMap<>();
		
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
		sendTask(workflow);
	}

}
