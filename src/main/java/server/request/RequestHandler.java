package main.java.server.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;

public class RequestHandler {

    private final TextRequestHandler textRequestHandler;
    private final ImageRequestHandler imageRequestHandler;

    @Inject
    public RequestHandler(
            TextRequestHandler textRequestHandler, 
            ImageRequestHandler imageRequestHandler) {

        this.textRequestHandler = textRequestHandler;
        this.imageRequestHandler = imageRequestHandler;
    }

    public void addRoutes(Javalin server) {
        textRequestHandler.addRoutes(server);
        imageRequestHandler.addRoutes(server);
    }

    public void addExceptionHandler(Javalin server) {
        // overrides default Javalin exception handler
        server.exception(ValidationException.class, (e, ctx) -> {
            List<ValidationError<Object>> combinedErrors = 
                    e.getErrors().get("REQUEST_BODY");

            List<Map<String, String>> codes = combinedErrors.stream()
                    .map(error -> Map.of(
                            "code", error.getMessage(),
                            "detail", error.getArgs().get("detail").toString()))
                    .collect(Collectors.toList());

            Object postRequest = combinedErrors.get(0).getValue();

            Map<String, Object> errorResponse = Map.of(
                    "errors", codes,
                    "postRequest", postRequest);
            ctx.json(errorResponse).status(400);
        });
    }

}
