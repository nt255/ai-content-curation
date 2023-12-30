package main.java.server.request;

import static java.net.HttpURLConnection.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;

public class RequestHandler {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(RequestHandler.class);

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
            
            LOG.info("found validation errors");
            ctx.json(errorResponse).status(HTTP_BAD_REQUEST);
        });
    }

}
