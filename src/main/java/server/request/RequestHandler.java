package main.java.server.request;

import java.time.Instant;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.validation.JavalinValidation;
import main.java.common.models.JobState;
import main.java.server.models.Image;
import main.java.server.models.Text;
import main.java.server.service.ImageService;
import main.java.server.service.TextService;


public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final int port;
    private final Javalin javalin;

    @Inject
    public RequestHandler(Properties properties, Gson gson, 
            TextService textService, ImageService imageService) {
        
        this.port = Integer.parseInt(properties.getProperty("javalin.port"));
        JavalinValidation.register(UUID.class, s -> UUID.fromString(s));

        javalin = Javalin.create()
                .get("/", ctx -> ctx.result("ai-content-curation"))
                
                
                // ----- TEXT -----
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
                    textService.insert(body);
                    ctx.json(gson.toJson(body));
                })

                .delete("/texts/{id}", ctx -> {
                    LOG.info("Received DELETE request to: {}", ctx.fullUrl());
                    UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
                    textService.delete(id);
                    ctx.status(204);
                })

                .post("/submit/texts/{id}", ctx -> {
                    LOG.info("Received POST request to: {}", ctx.fullUrl());
                    UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
                    textService.submit(id);
                    ctx.status(202);
                })
                
                
                // ----- IMAGE -----
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

    public void start() {
        javalin.start(port);
    }

}
