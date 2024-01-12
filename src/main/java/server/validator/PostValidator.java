package main.java.server.validator;

import java.util.ArrayList;
import java.util.List;

import main.java.server.models.post.CreatePostRequest;

public class PostValidator extends BaseValidator<CreatePostRequest> {
    
    public PostValidator() {
        super(CreatePostRequest.class);
    }

    @Override
    List<ErrorCode> validate(CreatePostRequest body) {
        List<ErrorCode> errorCodes = new ArrayList<>();
        return errorCodes;
    }

}