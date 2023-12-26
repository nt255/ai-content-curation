package main.java.processor.gpt;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.hexadevlabs.gpt4all.LLModel;

public class GPT4AllBinding {

    private static final Logger LOG = LoggerFactory.getLogger(GPT4AllBinding.class);
    private static final int DEFAULT_MAX_TOKENS = 4096;

    @Inject private GPT4AllLoader loader;

    public String generate(String prompt, Optional<Integer> maxTokens) {
        
        int numTokens = maxTokens.orElseGet(() -> DEFAULT_MAX_TOKENS);
        LOG.info("generating up to {} tokens..", numTokens);
        LOG.info("received the following prompt: {}", prompt);
        
        LLModel.GenerationConfig config = LLModel.config()
                .withNPredict(numTokens).build();

        // Will also stream to standard output
        String fullGeneration = loader.getDefaultModel().generate(prompt, config, true);
        System.out.println("\n");
        return fullGeneration;
    }

}
