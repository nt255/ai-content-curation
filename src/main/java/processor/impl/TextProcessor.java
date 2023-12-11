package main.java.processor.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.JobDao;
import main.java.common.db.models.JobDbModel;
import main.java.common.enums.JobState;
import main.java.processor.Processor;
import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResponse;
import main.java.processor.text.GPT4AllBinding;

public class TextProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TextProcessor.class);

    @Inject private GPT4AllBinding gpt4AllBinding;
    @Inject private JobDao dao;

    @Override
    public JobResponse doWork(JobRequest request) {

        UUID id = request.getId();
        String prompt = request.getPrompt();
        String result = gpt4AllBinding.generate(prompt, Optional.of(16));

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
