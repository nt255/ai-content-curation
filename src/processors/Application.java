/**
 * 
 */
package processors;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import processors.clients.ChatGPTClient;
import processors.impl.TextOnlyProcessor;
import processors.models.JobRequest;
import processors.models.JobResponse;

/**
 * 
 */
public class Application {
	
	private static Properties properties;
	private static Processor textOnlyProcessor;
	
	public static void init() {
				
		properties = new Properties();
		try {
			properties.load(new FileInputStream("src/config.properties"));
		} catch (IOException e) {
			System.err.println("failed to open properties file");
			e.printStackTrace();
		}
		
		ChatGPTClient chatGPTClient = new ChatGPTClient();
		textOnlyProcessor = new TextOnlyProcessor(chatGPTClient);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		init();
		
		JobRequest jobRequest = JobRequest.builder()
				.input("Write me a nice story about a girl on a farm.")
				.build();
		
		JobResponse jobResponse = textOnlyProcessor.doWork(jobRequest);
		
		System.out.println(jobResponse.getResult());

	}

}
