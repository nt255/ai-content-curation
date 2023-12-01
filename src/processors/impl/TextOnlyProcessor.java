package processors.impl;

import processors.Processor;
import processors.clients.ChatGPTClient;
import processors.models.JobRequest;
import processors.models.JobResponse;

public class TextOnlyProcessor implements Processor {
	
	private ChatGPTClient chatGPTClient;
	
	public TextOnlyProcessor(ChatGPTClient chatGPTClient) {
		this.chatGPTClient = chatGPTClient;
	}

	@Override
	public JobResponse doWork(JobRequest request) {
		
		String prompt = request.getInput();
		String result = chatGPTClient.makeRequest(prompt);
		
		JobResponse jobResponse = JobResponse.builder()
				.result(result)
				.build();
		
		return jobResponse;
	}

}
