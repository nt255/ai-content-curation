package processors.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import common.db.dao.JobDao;
import common.db.models.JobDbModel;
import common.enums.JobState;
import processors.Processor;
import processors.clients.ChatGPTClient;
import processors.models.JobRequest;
import processors.models.JobResponse;

public class TextOnlyProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TextOnlyProcessor.class);

    @Inject private ChatGPTClient chatGPTClient;
    @Inject private JobDao dao;

    @Override
    public JobResponse doWork(JobRequest request) {

        UUID id = request.getId();
        String prompt = request.getPrompt();
        String result = chatGPTClient.makeRequest(prompt);

        LOG.info("produced result: {} from ChatGPT client..", result);
        
        // TODO: do this correctly
        JobDbModel existing = dao.get(id).get();
        existing.setLastModifiedOn(Instant.now());
        existing.setState(JobState.COMPLETED);
        existing.setTextResult(result);
        dao.delete(id);
        dao.insert(existing);

        JobResponse jobResponse = JobResponse.builder()
                .id(request.getId())
                .isSuccessful(true)
                .errors(List.of())
                .build();

        return jobResponse;
    }

}
