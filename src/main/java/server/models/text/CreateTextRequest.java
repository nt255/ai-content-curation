package main.java.server.models.text;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.text.TextParams;
import main.java.server.models.BaseCreateRequest;

@Getter
@Setter
@SuperBuilder
public class CreateTextRequest extends BaseCreateRequest {
    
    // input
    private final List<TextParams> params;
    
    private boolean isServiceCall;

}
