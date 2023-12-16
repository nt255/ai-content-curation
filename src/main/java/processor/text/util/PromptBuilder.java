package main.java.processor.text.util;

import lombok.Builder;
import main.java.common.models.text.TextParamsType;

@Builder
public class PromptBuilder {   // experimental

    private StringBuilder sb;
    
    private TextParamsType type;
    private String prompt;
    private String audience;

    public String getFinalPrompt() {
        sb = new StringBuilder();
        
        if (TextParamsType.CREATE_HASHTAGS.equals(type))
            sb.append("Generate several hashtags about the following: ");
        sb.append(prompt);
        sb.append(".");

        if (audience != null)
            sb.append("\nAppeal to " + audience + ".");

        return sb.toString();
    }

}
