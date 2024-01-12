package main.java.server.validator;

import java.util.ArrayList;
import java.util.List;

import main.java.common.models.text.TextParams;
import main.java.server.models.text.CreateTextRequest;

public class TextValidator extends BaseValidator<CreateTextRequest> {
    
    public TextValidator() {
        super(CreateTextRequest.class);
    }

    @Override
    List<ErrorCode> validate(CreateTextRequest body) {
        List<ErrorCode> errorCodes = new ArrayList<>();
        
        List<TextParams> params = body.getParams();
        if (params.size() > 1)
            errorCodes.add(ErrorCode.MULTIPLE_STEPS_NOT_SUPPORTED);
        
        return errorCodes;
    }

}
