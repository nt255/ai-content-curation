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
    private final List<TextParamsType> steps;
    private final List<TextParams> params;

    // output
    private final String outputText;

}
