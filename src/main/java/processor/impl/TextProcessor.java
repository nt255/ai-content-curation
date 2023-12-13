package main.java.processor.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.processor.Processor;
import main.java.processor.models.ProcessorResponse;
import main.java.processor.text.GPT4AllBinding;

public class TextProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TextProcessor.class);

    @Inject private GPT4AllBinding gpt4AllBinding;

    @Override
    public ProcessorResponse doWork(UUID id, Map<String, String> params) {

        String prompt = params.get("prompt");
        String outputText = gpt4AllBinding.generate(prompt, Optional.of(2));

        LOG.info("produced result: {} from GPT4All.", outputText);

        ProcessorResponse result = ProcessorResponse.builder()
                .id(id)
                .isSuccessful(true)
                .outputString(outputText)
                .errors(List.of())
                .build();

        return result;
    }

}
