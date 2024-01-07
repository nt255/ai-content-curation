package main.java.processor.text;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.TextDao;
import main.java.common.models.text.TextParams;
import main.java.common.models.text.TextParamsType;
import main.java.processor.MultistepProcessor;
import main.java.processor.gpt.GPT4AllBinding;
import main.java.processor.models.ProcessorResult;
import main.java.processor.text.util.HashtagCleaner;
import main.java.processor.text.util.PromptBuilder;

public class TextProcessor implements MultistepProcessor<TextParams> {
    
    private static final Logger LOG = LoggerFactory.getLogger(TextProcessor.class);

    private final GPT4AllBinding gpt4AllBinding;
    private final HashtagCleaner hashtagCleaner;
    private final TextDao textDao;
    
    @Inject
    private TextProcessor(GPT4AllBinding gpt4AllBinding, 
            HashtagCleaner hashtagCleaner, TextDao textDao) {
        
        this.gpt4AllBinding = gpt4AllBinding;
        this.hashtagCleaner = hashtagCleaner;
        this.textDao = textDao;
    }

    @Override
    public ProcessorResult process(UUID id, List<TextParams> steps) {
        if (steps.size() != 1)
            throw new UnsupportedOperationException();
        
        TextParams params = steps.get(0);

        TextParamsType type = params.getType();
        String prompt = params.getPrompt();
        Integer numTokens = params.getNumTokens();

        if (TextParamsType.CREATE_HASHTAGS.equals(type)) {
            prompt = PromptBuilder.builder()
                    .type(TextParamsType.CREATE_HASHTAGS)
                    .prompt(prompt)
                    .audience(params.getAudience())
                    .build()
                    .getFinalPrompt();
            numTokens = 100;
        }

        String outputText = gpt4AllBinding.generate(
                prompt, Optional.ofNullable(numTokens));

        if (TextParamsType.CREATE_HASHTAGS.equals(type))
            outputText = hashtagCleaner.clean(outputText);

        ProcessorResult result = ProcessorResult.builder()
                .id(id)
                .outputString(outputText)
                .build();

        return result;
    }

    @Override
    public void save(UUID id, ProcessorResult result) {
        textDao.update(id, result.getOutputString());
        LOG.info("Save successful.");
    }

}
