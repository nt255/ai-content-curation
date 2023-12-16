package main.java.server.request;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.javalin.Javalin;
import main.java.common.models.JobState;
import main.java.server.models.Text;
import main.java.server.service.TextService;

public class TextRequestHandler {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(TextRequestHandler.class);
    
    private final TextService textService;
    private final Gson gson;
    
    @Inject
    public TextRequestHandler(TextService textService, Gson gson) {
        this.textService = textService;
        this.gson = gson;
    }
    
    public void addRoutes(Javalin server) {
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
            Text body = gson.fromJson(bodyString, Text.class);
            body.setId(UUID.randomUUID());
            body.setCreatedOn(Instant.now());
            body.setLastModifiedOn(Instant.now());
            body.setState(JobState.NEW);
            textService.create(body);
            ctx.json(gson.toJson(body)).status(202);
        })

        .delete("/texts/{id}", ctx -> {
            LOG.info("Received DELETE request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            textService.delete(id);
            ctx.status(204);
        });

    }

}
