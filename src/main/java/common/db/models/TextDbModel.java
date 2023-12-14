package main.java.common.db.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.TextParams;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TextDbModel extends JobDbModel {

    // input
    private TextParams params;

    // output
    private String outputText;

}
