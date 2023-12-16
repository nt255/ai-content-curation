package main.java.server.models.text;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.text.TextParams;
import main.java.common.models.text.TextParamsType;
import main.java.server.models.BasePostRequest;

@Getter
@Setter
@SuperBuilder
public class PostTextRequest extends BasePostRequest {
    
    // input
    private List<TextParamsType> steps;
    private List<TextParams> params;

}
