package main.java.server.request;

import com.google.inject.Inject;

import main.java.server.models.text.PostTextRequest;
import main.java.server.service.TextService;
import main.java.server.validator.TextValidator;

class TextRequestHandler extends 
BaseRequestHandler<PostTextRequest, TextService, TextValidator> {
    
    @Inject
    public TextRequestHandler(TextService textService, 
            TextValidator textValidator) {
        super(textService, textValidator, "/texts");
    }

}
