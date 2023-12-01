package processors.clients;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class ComfyClient {

	public static void main(String[] args) {
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

	}

}
