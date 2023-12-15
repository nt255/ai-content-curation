package main.java.common.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TextParams extends BaseParams {
    
    public enum TextType {
        PLAIN, HASHTAGS
    }
    
    private TextType type;
    
    private String audience;
    
    private Integer numTokens;

}
