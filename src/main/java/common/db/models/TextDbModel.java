package main.java.common.db.models;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.text.TextParams;
import main.java.common.models.text.TextParamsType;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TextDbModel extends JobDbModel {

    // input
    private List<TextParamsType> steps;
    private List<TextParams> params;

    // output
    private String outputText;

}
