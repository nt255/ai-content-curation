package main.java.server.request;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.javalin.Javalin;
import main.java.server.models.text.PostTextRequest;
import main.java.server.service.TextService;

class TextRequestHandler {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(TextRequestHandler.class);
    
    private final TextService textService;
    private final Gson gson;
    
    @Inject
    public TextRequestHandler(TextService textService, Gson gson) {
        this.textService = textService;
        this.gson = gson;
    }
    
    void addRoutes(Javalin server) {
        server
        .get("/texts/{id}", ctx -> {
            LOG.info("Received GET request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            textService.get(id).ifPresentOrElse(
                    job -> ctx.json(gson.toJson(job)), 
                    () -> ctx.status(404));
        })

        .post("/texts", ctx -> {
            String bodyString = ctx.body();
            LOG.info("Received POST request to: {}, body: {}", ctx.fullUrl(), bodyString);
            PostTextRequest body = gson.fromJson(bodyString, PostTextRequest.class);
            UUID generatedId = textService.create(body);
            ctx.result(generatedId.toString()).status(202);
        })

        .delete("/texts/{id}", ctx -> {
            LOG.info("Received DELETE request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            textService.delete(id);
            ctx.status(204);
        });

    }

}
