package main.java.common.db.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.enums.TextType;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TextDbModel extends JobDbModel {
    
    private TextType type;
    private String outputText;
    
}
