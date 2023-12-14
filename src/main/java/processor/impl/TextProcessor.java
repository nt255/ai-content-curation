package main.java.processor.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.TextDao;
import main.java.common.db.models.TextDbModel;
import main.java.common.models.JobState;
import main.java.common.models.TextParams;
import main.java.processor.Processor;
import main.java.processor.models.ProcessorResult;
import main.java.processor.text.GPT4AllBinding;


public class TextProcessor implements Processor<TextParams> {

    private static final Logger LOG = LoggerFactory.getLogger(TextProcessor.class);

    @Inject private TextDao textDao;
    @Inject private GPT4AllBinding gpt4AllBinding;

    @Override
    public ProcessorResult process(UUID id, TextParams params) {

        String prompt = params.getPrompt();
        String outputText = gpt4AllBinding.generate(prompt, Optional.of(2));

        LOG.info("produced result: {} from GPT4All.", outputText);

        ProcessorResult result = ProcessorResult.builder()
                .id(id)
                .isSuccessful(true)
                .outputString(outputText)
                .errors(List.of())
                .build();

        return result;
    }

    public void save(UUID id, ProcessorResult result) {
        TextDbModel existing = textDao.get(id).get();
        existing.setLastModifiedOn(Instant.now());
        existing.setState(JobState.COMPLETED);
        existing.setOutputText(result.getOutputString());
        
        textDao.delete(id);
        textDao.insert(existing);
    }

}
