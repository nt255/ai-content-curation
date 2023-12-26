package main.java.server.request;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.javalin.Javalin;
import main.java.server.models.image.PostImageRequest;
import main.java.server.service.ImageService;


class ImageRequestHandler {

    private static final Logger LOG = 
            LoggerFactory.getLogger(ImageRequestHandler.class);
    
    private final ImageService imageService;
    private final Gson gson;
    
    @Inject
    public ImageRequestHandler(ImageService imageService, Gson gson) {
        this.imageService = imageService;
        this.gson = gson;
    }

    void addRoutes(Javalin server) {
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
            PostImageRequest body = gson.fromJson(bodyString, PostImageRequest.class);
            UUID generatedId = imageService.create(body);
            ctx.result(generatedId.toString()).status(202);
        })
        
        .delete("/images/{id}", ctx -> {
            LOG.info("Received DELETE request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            imageService.delete(id);
            ctx.status(204);
        });
    }

}
