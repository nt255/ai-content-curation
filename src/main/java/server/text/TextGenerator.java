package main.java.server.text;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class TextGenerator {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(TextGenerator.class);

    private final OpenAiChatModel model;
    
    @Inject
    public TextGenerator(Properties properties) {
        String apiKey = properties.getProperty("openai.secretkey");
        this.model = OpenAiChatModel.withApiKey(apiKey);
    }
    
    public String generate(String prompt) {
        LOG.info("begin generation..");
        String outputText = model.generate(prompt);
        LOG.info("generated text: {}", outputText);
        
        return outputText;
    }

}
