package main.java.server.request;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.javalin.Javalin;
import main.java.server.models.image.PostImageRequest;
import main.java.server.service.ImageService;
import main.java.server.validator.ImageValidator;

class ImageRequestHandler {

    private static final Logger LOG = 
            LoggerFactory.getLogger(ImageRequestHandler.class);
    
    private final ImageValidator imageValidator;
    private final ImageService imageService;
    
    @Inject
    public ImageRequestHandler(ImageValidator imageValidator,
            ImageService imageService) {
        
        this.imageValidator = imageValidator;
        this.imageService = imageService;
    }

    void addRoutes(Javalin server) {
        server
        .get("/images/{id}", ctx -> {
            LOG.info("Received GET request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            imageService.get(id).ifPresentOrElse(
                    job -> ctx.json(job), 
                    () -> ctx.status(404));
        })
        
        .post("/images", ctx -> {
            LOG.info("Received POST request to: {}", ctx.fullUrl());
            PostImageRequest body = imageValidator.validate(ctx).get();
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
