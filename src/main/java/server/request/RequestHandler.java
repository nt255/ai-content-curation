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
import main.java.common.enums.JobState;
import main.java.server.models.Job;
import main.java.server.service.JobService;

public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final int port;
    private final Javalin javalin;

    @Inject
    public RequestHandler(Properties properties, Gson gson, JobService jobService) {
        this.port = Integer.parseInt(properties.getProperty("javalin.port"));

        // todo: move elsewhere
        JavalinValidation.register(UUID.class, s -> UUID.fromString(s));

        javalin = Javalin.create()
                .get("/", ctx -> ctx.result("ai-content-curation"))

                .get("/jobs/{id}", ctx -> {
                    LOG.info("Received GET request to: {}", ctx.fullUrl());
                    UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();

                    jobService.get(id).ifPresentOrElse(
                            job -> ctx.json(gson.toJson(job)), 
                            () -> ctx.status(404));
                })

                .post("/jobs", ctx -> {
                    String bodyString = ctx.body();
                    LOG.info("Received POST request to: {}, body: {}", ctx.fullUrl(), bodyString);
                    Job body = gson.fromJson(bodyString, Job.class);

                    body.setId(UUID.randomUUID());
                    body.setCreatedOn(Instant.now());
                    body.setLastModifiedOn(Instant.now());
                    body.setState(JobState.WAITING);

                    jobService.insert(body);
                    ctx.json(gson.toJson(body));
                })

                .delete("/jobs/{id}", ctx -> {
                    LOG.info("Received DELETE request to: {}", ctx.fullUrl());
                    UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();

                    jobService.delete(id);
                    ctx.status(204);
                })

                .post("/submit/{id}", ctx -> {
                    LOG.info("Received POST request to: {}", ctx.fullUrl());
                    UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();

                    jobService.submit(id);
                    ctx.status(202);
                });
    }

    public void start() {
        javalin.start(port);
    }

}
