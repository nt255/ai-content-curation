package main.java.server.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.enums.TextType;

@Getter
@Setter
@SuperBuilder
public class Text extends Job {

    private TextType type;
    private String outputText;

}
