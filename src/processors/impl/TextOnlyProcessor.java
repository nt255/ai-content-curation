package processors.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import common.db.dao.JobDAO;
import common.db.models.Job;
import common.enums.JobState;
import common.enums.JobType;
import processors.Processor;
import processors.clients.ChatGPTClient;
import processors.models.JobRequest;
import processors.models.JobResponse;

public class TextOnlyProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TextOnlyProcessor.class);

    @Inject private ChatGPTClient chatGPTClient;
    @Inject private JobDAO dao;

    @Override
    public JobResponse doWork(JobRequest request) {

        UUID id = request.getId();
        String prompt = request.getPrompt().get();
        String result = chatGPTClient.makeRequest(prompt);

        LOG.info("produced result: {} from ChatGPT client..", result);
        
        dao.insert(Job.builder()
                .jobType(JobType.TEXT_ONLY)
                .jobState(JobState.COMPLETED)
                .id(id)
                .textOnlyResult(result)
                .build());

        JobResponse jobResponse = JobResponse.builder()
                .id(request.getId())
                .isSuccessful(true)
                .errors(List.of())
                .build();

        return jobResponse;
    }

}
