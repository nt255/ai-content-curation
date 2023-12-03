package processors.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import processors.Processor;
import processors.clients.ChatGPTClient;
import processors.models.JobRequest;
import processors.models.JobResponse;

public class TextOnlyProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TextOnlyProcessor.class);

    @Inject private ChatGPTClient chatGPTClient;

    @Override
    public JobResponse doWork(JobRequest request) {

        String prompt = request.getPrompt().get();
        String result = chatGPTClient.makeRequest(prompt);
        
        LOG.info("produced result: {} from ChatGPT client..", result);
        // TODO: put results into DB here

        JobResponse jobResponse = JobResponse.builder()
                .id(request.getId())
                .isSuccessful(true)
                .errors(List.of())
                .build();

        return jobResponse;
    }

}
