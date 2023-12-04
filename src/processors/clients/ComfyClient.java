package processors.clients;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import java.util.Properties;
import common.clients.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class ComfyClient {
	
	@Inject private Properties properties;
    @Inject private HttpClient httpClient;
    
    private Map<String, String> loadDefaultConfigs() {
    	Map<String, String> configs = new HashMap<String, String>();
    	configs.put("checkpoint", properties.getProperty("comfyui.checkpoint"));
    	configs.put("latentHeight", properties.getProperty("comfyui.latent.height"));
    	configs.put("latentWidth", properties.getProperty("comfyui.latent.width"));
    	configs.put("loraOne", properties.getProperty("comfyui.loras.one"));
    	configs.put("loraTwo", properties.getProperty("comfyui.loras.two"));
    	configs.put("loraThree", properties.getProperty("comfyui.loras.three"));
    	configs.put("prompt", properties.getProperty("comfyui.prompt.default"));
    	configs.put("savePath", properties.getProperty("comfyui.output.path"));
    	return configs;
    }
    
    // applies configurations to a given workflow.
    private void applyConfigs(Map<String, Object> promptWorkflow, Map<String, String> configs) {
    	 Map<String, Object> chkpointLoaderNode = (Map<String, Object>) promptWorkflow.get("1");
         Map<String, Object> latentImageNode = (Map<String, Object>) promptWorkflow.get("6");
         Map<String, Object> loraOneNode = (Map<String, Object>) promptWorkflow.get("4");
         Map<String, Object> loraTwoNode = (Map<String, Object>) promptWorkflow.get("7");
         Map<String, Object> loraThreeNode = (Map<String, Object>) promptWorkflow.get("8");
         Map<String, Object> posNode = (Map<String, Object>) promptWorkflow.get("9");
         Map<String, Object> negNode = (Map<String, Object>) promptWorkflow.get("10");
         Map<String, Object> ksamplerNode = (Map<String, Object>) promptWorkflow.get("2");
         Map<String, Object> saveImageNode = (Map<String, Object>) promptWorkflow.get("14");
         if (chkpointLoaderNode != null) {
             Map<String, Object> inputs = (Map<String, Object>) chkpointLoaderNode.get("inputs");
             if (inputs != null) {
                 inputs.put("ckpt_name", configs.get("checkpoint"));
             }
         }
    }
    
	
	// will load in default options when queued without arguments
	public void queuePrompt() throws IOException {
		System.out.println("this is queuePrompt with no args.");
		Map<String, String> configs = loadDefaultConfigs();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			File jsonFile = new File("src/processors/clients/workflow.json");
            Map<String, Object> promptWorkflow = objectMapper.readValue(jsonFile, Map.class);
            System.out.println(promptWorkflow);
            try {
                // Accessing specific nodes based on their keys
               applyConfigs(promptWorkflow, configs);
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		configs.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
	}

	public void queuePrompt(String[] args) {
		System.out.println("this is queuePrompt with string[] args.");
		
		// parsing arguments as key-value pairs. we can manage more settings if needed.
		Map<String, String> keyValuePairs = new HashMap<>();
		for (String arg: args) {
			String[] keyValue = arg.split(":");
			
			if (keyValue.length == 2) {
				keyValuePairs.put(keyValue[0], keyValue[1]);
			} else {
				// exits the program if the arguments are invalid. 
				// we can change this to integrate better with the pipeline later.
				System.out.println("Invalid argument format: " + arg);
				System.out.println("Exiting the program.");
				System.exit(1);
			}
		}
		
		// access values via keys
		for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			System.out.println("KEY (OPTION): " + key + ", VALUE: " + value);
		}
		
		// overwrites defaults 
	}

}
