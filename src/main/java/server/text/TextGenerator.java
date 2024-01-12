package main.java.server.text;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import dev.langchain4j.model.openai.OpenAiChatModel;

import lombok.Builder;
import lombok.Getter;
import main.java.common.models.text.TextParams;
import main.java.common.models.text.TextParamsType;
import main.java.common.util.text.HashtagCleaner;
import main.java.common.util.text.PromptBuilder;

public class TextGenerator {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(TextGenerator.class);
    
    @Builder
    @Getter
    public static class Result {
        private final String outputText;
    }

    private final OpenAiChatModel model;
    private final HashtagCleaner hashtagCleaner;
    
    @Inject
    public TextGenerator(Properties properties, 
            HashtagCleaner hashtagCleaner) {
        String apiKey = properties.getProperty("openai.secretkey");
        this.model = OpenAiChatModel.withApiKey(apiKey);
        this.hashtagCleaner = hashtagCleaner;
    }
    
    public Result generate(TextParams params) {
        TextParamsType type = params.getType();
        if (TextParamsType.CREATE.equals(type))
            return generatePlain(params);
        if (TextParamsType.CREATE_HASHTAGS.equals(type))
            return generateHashtags(params);
        
        throw new UnsupportedOperationException();
    }
    
    private Result generatePlain(TextParams params) {
        return Result.builder()
                .outputText(generate(params.getPrompt()))
                .build();
    }
    
    private Result generateHashtags(TextParams params) {
        String prompt = PromptBuilder.builder()
                .type(TextParamsType.CREATE_HASHTAGS)
                .prompt(params.getPrompt())
                .audience(params.getAudience())
                .build()
                .getFinalPrompt();
        
        String hashtags = 
                hashtagCleaner.clean(generate(prompt));
                
        return Result.builder()
                .outputText(hashtags)
                .build();
    }
    
    private String generate(String prompt) {
        LOG.info("begin generation..");
        String outputText = model.generate(prompt);
        LOG.info("generated text: {}", outputText);
        return outputText;
    }

}
