package test.java.processor.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import main.java.common.models.text.TextParams;
import main.java.common.models.text.TextParamsType;
import main.java.processor.models.ProcessorResult;
import main.java.processor.text.TextProcessor;
import test.java.TestWithInjections;

public class TextProcessorTests extends TestWithInjections {

    @Inject private TextProcessor textProcessor;

    
    @Test
    void optionalNumTokens() {

        Integer numTokens = 16;

        UUID id = UUID.randomUUID();
        TextParams params = TextParams.builder()
                .type(TextParamsType.CREATE)
                .prompt("few short sentences")
                .numTokens(numTokens)
                .build();

        ProcessorResult result = textProcessor.process(id, List.of(params));

        StringTokenizer tokenizer = 
                new StringTokenizer(result.getOutputString(), " ");

        assertTrue(numTokens > tokenizer.countTokens(), 
                "generated more tokens than passed in");
    }

}
