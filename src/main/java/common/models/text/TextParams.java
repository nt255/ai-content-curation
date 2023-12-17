package main.java.common.models.text;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.BaseParams;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TextParams extends BaseParams {
    
    private TextParamsType type;
    
    private String audience;
    private Integer numTokens;

}
