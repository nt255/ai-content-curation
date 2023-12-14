package main.java.server.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.TextParams;

@Getter
@Setter
@SuperBuilder
public class Text extends Job {

    private TextParams params;
    
    // output
    private String outputText;

}
