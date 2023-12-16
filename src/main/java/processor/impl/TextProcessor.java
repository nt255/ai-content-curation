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
import main.java.common.models.TextParams.TextType;
import main.java.processor.Processor;
import main.java.processor.models.ProcessorResult;
// import main.java.processor.text.ChatGPTClient;
import main.java.processor.text.GPT4AllBinding;
import main.java.processor.text.util.HashtagCleaner;
import main.java.processor.text.util.PromptBuilder;


public class TextProcessor implements Processor<TextParams> {

    private static final Logger LOG = LoggerFactory.getLogger(TextProcessor.class);

    @Inject private TextDao textDao;
    @Inject private HashtagCleaner hashtagCleaner;

    @Inject private GPT4AllBinding gpt4AllBinding;
    // @Inject private ChatGPTClient chatGPTClient;


    @Override
    public ProcessorResult process(UUID id, TextParams params) {

        TextType type = params.getType();
        String prompt = params.getPrompt();
        Integer numTokens = params.getNumTokens();

        if (TextType.HASHTAGS.equals(type)) {
            prompt = PromptBuilder.builder()
                    .textType(TextType.HASHTAGS)
                    .prompt(prompt)
                    .audience(params.getAudience())
                    .build()
                    .getFinalPrompt();
            numTokens = 100;
        }

        String outputText = gpt4AllBinding.generate(
                prompt, Optional.ofNullable(numTokens));
//        String outputText = chatGPTClient.generate(prompt);
        LOG.info("produced result: {}", outputText);

        if (TextType.HASHTAGS.equals(type))
            outputText = hashtagCleaner.clean(outputText);

        ProcessorResult result = ProcessorResult.builder()
                .id(id)
                .isSuccessful(true)
                .outputString(outputText)
                .errors(List.of())
                .build();

        return result;
    }

    @Override
    public void save(UUID id, ProcessorResult result) {
        TextDbModel existing = textDao.get(id).get();
        existing.setLastModifiedOn(Instant.now());
        existing.setState(JobState.COMPLETED);
        existing.setOutputText(result.getOutputString());

        textDao.delete(id);
        textDao.insert(existing);
    }

}
