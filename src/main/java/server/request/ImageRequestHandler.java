package main.java.server.request;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.javalin.Javalin;
import main.java.common.models.JobState;
import main.java.server.models.Image;
import main.java.server.service.ImageService;

public class ImageRequestHandler {

    private static final Logger LOG = 
            LoggerFactory.getLogger(ImageRequestHandler.class);
    
    private final ImageService imageService;
    private final Gson gson;
    
    @Inject
    public ImageRequestHandler(ImageService imageService, Gson gson) {
        this.imageService = imageService;
        this.gson = gson;
    }

    public void addRoutes(Javalin server) {
        server
        .get("/images/{id}", ctx -> {
            LOG.info("Received GET request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            imageService.get(id).ifPresentOrElse(
                    job -> ctx.json(gson.toJson(job)), 
                    () -> ctx.status(404));
        })
        
        .post("/images", ctx -> {
            String bodyString = ctx.body();
            LOG.info("Received POST request to: {}, body: {}", ctx.fullUrl(), bodyString);
            Image body = gson.fromJson(bodyString, Image.class);
            body.setId(UUID.randomUUID());
            body.setCreatedOn(Instant.now());
            body.setLastModifiedOn(Instant.now());
            body.setState(JobState.NEW);
            imageService.insert(body);
            ctx.json(gson.toJson(body));
        })
        
        .delete("/images/{id}", ctx -> {
            LOG.info("Received DELETE request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            imageService.delete(id);
            ctx.status(204);
        })
        
        .post("/submit/images/{id}", ctx -> {
            LOG.info("Received POST request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            imageService.submit(id);
            ctx.status(202);
        });
    }

}
