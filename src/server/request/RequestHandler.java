package server.request;

import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.validation.JavalinValidation;
import server.models.Job;
import server.service.JobService;

public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final int port;
    private final JobService jobService;
    private final Javalin javalin;

    @Inject
    public RequestHandler(Properties properties, JobService jobService) {
        this.port = Integer.parseInt(properties.getProperty("javalin.port"));
        this.jobService = jobService;

        // todo: move elsewhere
        JavalinValidation.register(UUID.class, s -> UUID.fromString(s));

        javalin = Javalin.create()

                .get("/", ctx -> ctx.result("Hello World"))
                
                .get("/jobs/{id}", ctx -> getJob(ctx))

                .post("/jobs", ctx -> {
                    jobService.insert(ctx.bodyAsClass(Job.class));
                })

                .delete("/jobs", ctx -> {
                    jobService.delete(ctx.pathParamAsClass("{id}", UUID.class).get());
                })

                .post("/submit/{job-id}", ctx -> {
                    jobService.submitJob(ctx.pathParamAsClass("{job-id}", UUID.class).get());
                });

    }

    public void start() {
        javalin.start(port);
    }
    
    private void getJob(Context ctx) {
        LOG.info("Received GET request to: {}", ctx.fullUrl());
        UUID jobId = ctx.pathParamAsClass("{id}", UUID.class).get();
        jobService.get(jobId).ifPresentOrElse(
                job -> ctx.json(new Gson().toJson(job)), 
                () -> ctx.status(400));
    }

}
