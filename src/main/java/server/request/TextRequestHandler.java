package main.java.server.request;

import static java.net.HttpURLConnection.*;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.javalin.Javalin;
import main.java.server.models.text.PostTextRequest;
import main.java.server.service.TextService;
import main.java.server.validator.TextValidator;

class TextRequestHandler {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(TextRequestHandler.class);
    
    private final TextValidator textValidator;
    private final TextService textService;
    
    @Inject
    public TextRequestHandler(TextValidator textValidator, 
            TextService textService) {
        
        this.textValidator = textValidator;
        this.textService = textService;
    }
    
    void addRoutes(Javalin server) {
        server
        .get("/texts/{id}", ctx -> {
            LOG.info("Received GET request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            textService.get(id).ifPresentOrElse(
                    job -> ctx.json(job).status(HTTP_OK), 
                    () -> ctx.status(HTTP_NOT_FOUND));
        })

        .post("/texts", ctx -> {
            LOG.info("Received POST request to: {}", ctx.fullUrl());
            PostTextRequest body = textValidator.validate(ctx).get();
            UUID generatedId = textService.create(body);
            ctx.result(generatedId.toString()).status(HTTP_ACCEPTED);
        })

        .delete("/texts/{id}", ctx -> {
            LOG.info("Received DELETE request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            textService.delete(id);
            ctx.status(HTTP_NO_CONTENT);
        });
    }

}
