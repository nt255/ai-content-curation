package main.java.server.validator;

import io.javalin.http.Context;
import io.javalin.validation.BodyValidator;
import main.java.server.models.text.PostTextRequest;

public class TextValidator extends BaseValidator<PostTextRequest> {

    @Override
    public BodyValidator<PostTextRequest> validate(Context ctx) {
        return ctx.bodyValidator(PostTextRequest.class);
    }

}
