package main.java.server.validator;

import java.util.List;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.validation.BodyValidator;
import io.javalin.validation.ValidationError;
import main.java.server.models.BasePostRequest;

public abstract class BaseValidator<T extends BasePostRequest> {
    
    private final Class<T> typeParameterClass;
    
    public BaseValidator(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public final BodyValidator<T> validate(Context ctx) {

        // Unfortunately with Javalin's validation setup, checks are expressed
        // as Predicate -> ValidationError mappings. This makes it hard to
        // return specific error messages without having a lot of duplicated
        // code (e.g. iterating through List<ImageParams>.
        //
        // Instead, we unpack postImageRequest from ctx first, run one large
        // validation method, get all the errors, then for each of them add a 
        // check that will always fail to the original ctx. Either this or we'd 
        // have to handle errors manually outside of the exception handler.
        
        BodyValidator<T> bodyValidator = ctx.bodyValidator(typeParameterClass);
        
        T postRequest = ctx.bodyAsClass(typeParameterClass);
        List<ErrorCode> errorCodes = validate(postRequest);
        
        errorCodes.stream()
        .map(this::getValidationError)
        .forEach(e -> bodyValidator.check(ignored -> false, e));

        return bodyValidator;
    }
    
    abstract List<ErrorCode> validate(T body);

    private ValidationError<T> getValidationError(ErrorCode code) {
        return new ValidationError<T>(code.name(), 
                Map.of("detail", code.detail), null);
    }

}
