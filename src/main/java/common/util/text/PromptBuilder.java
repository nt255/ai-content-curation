package main.java.common.util.text;

import lombok.Builder;
import main.java.common.models.text.TextParamsType;

@Builder
public class PromptBuilder {   // experimental

    private StringBuilder sb;
    
    private final TextParamsType type;
    private final String prompt;
    private final String audience;

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
