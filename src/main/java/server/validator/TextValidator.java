package main.java.server.validator;

import java.util.List;

import main.java.server.models.text.PostTextRequest;

public class TextValidator extends BaseValidator<PostTextRequest> {
    
    public TextValidator() {
        super(PostTextRequest.class);
    }

    @Override
    List<ErrorCode> validate(PostTextRequest body) {
        return List.of();
    }

}
