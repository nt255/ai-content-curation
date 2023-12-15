package main.java.processor.text.util;

import lombok.Builder;
import main.java.common.models.TextParams.TextType;

@Builder
public class PromptBuilder {   // experimental

    private StringBuilder sb;
    
    private TextType textType;
    private String prompt;
    private String audience;

    public String getFinalPrompt() {
        sb = new StringBuilder();
        
        if (TextType.HASHTAGS.equals(textType))
            sb.append("Generate several hashtags about the following: ");
        sb.append(prompt);
        sb.append(".");

        if (audience != null)
            sb.append("\nAppeal to " + audience + ".");

        return sb.toString();
    }

}
