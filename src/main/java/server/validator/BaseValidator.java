package main.java.server.validator;

import java.util.Map;

import io.javalin.http.Context;
import io.javalin.validation.BodyValidator;
import io.javalin.validation.ValidationError;
import main.java.server.models.BasePostRequest;

abstract class BaseValidator<T extends BasePostRequest> {

    @SuppressWarnings("unused")
    public BodyValidator<T> validate(Context ctx) {
        throw new UnsupportedOperationException();
    }

    final ValidationError<T> getValidationError(ErrorCode code) {
        return new ValidationError<T>(code.name(), 
                Map.of("detail", code.detail), null);
    }

}
