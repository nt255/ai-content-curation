package main.java.server.models.text;

import java.util.List;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.text.TextParams;
import main.java.common.models.text.TextParamsType;
import main.java.server.models.GetJobResponse;

@Getter
@SuperBuilder
public class GetTextResponse extends GetJobResponse {

    // input
    private List<TextParamsType> steps;
    private List<TextParams> params;

    // output
    private String outputText;

}
