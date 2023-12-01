/**
 * 
 */
package processors;

import processors.clients.impl.ChatGPTClient;
import processors.impl.TextOnlyProcessor;
import processors.models.JobRequest;
import processors.models.JobResponse;

/**
 * 
 */
public class Application {
	
	private static Processor textOnlyProcessor;
	
	public static void init() {
		ChatGPTClient chatGPTClient = new ChatGPTClient();
		textOnlyProcessor = new TextOnlyProcessor(chatGPTClient);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		init();
		
		JobRequest jobRequest = new JobRequest();
		jobRequest.setInput("Write me a nice story about a girl on a farm.");
		JobResponse jobResponse = textOnlyProcessor.doWork(jobRequest);
		
		System.out.println(jobResponse.getResult());

	}

}
