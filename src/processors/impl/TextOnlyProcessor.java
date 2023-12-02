package processors.impl;

import com.google.inject.Inject;

import processors.Processor;
import processors.clients.ChatGPTClient;
import processors.models.JobRequest;
import processors.models.JobResponse;

public class TextOnlyProcessor implements Processor {
	
	@Inject private ChatGPTClient chatGPTClient;

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
