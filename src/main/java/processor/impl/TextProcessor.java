package main.java.processor.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.processor.Processor;
import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResult;
import main.java.processor.text.GPT4AllBinding;

public class TextProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TextProcessor.class);

    @Inject private GPT4AllBinding gpt4AllBinding;

    @Override
    public JobResult doWork(JobRequest request) {

        String prompt = request.getPrompt();
        String outputText = gpt4AllBinding.generate(prompt, Optional.of(2));

        LOG.info("produced result: {} from GPT4All.", outputText);

        JobResult result = JobResult.builder()
                .id(request.getId())
                .isSuccessful(true)
                .outputText(outputText)
                .errors(List.of())
                .build();

        return result;
    }

}
