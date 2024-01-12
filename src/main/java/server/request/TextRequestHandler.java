package main.java.server.request;

import com.google.inject.Inject;

import main.java.server.models.text.CreateTextRequest;
import main.java.server.service.TextService;
import main.java.server.validator.TextValidator;

class TextRequestHandler extends 
BaseRequestHandler<CreateTextRequest, TextService, TextValidator> {
    
    @Inject
    public TextRequestHandler(TextService textService, 
            TextValidator textValidator) {
        super(textService, textValidator, "/texts");
    }

}
