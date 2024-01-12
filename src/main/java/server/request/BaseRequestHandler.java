package main.java.server.request;

import static java.net.HttpURLConnection.*;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import main.java.server.models.BaseCreateRequest;
import main.java.server.service.BaseService;
import main.java.server.validator.BaseValidator;

abstract class BaseRequestHandler<
S extends BaseCreateRequest,
T extends BaseService<?, S, ?>, 
U extends BaseValidator<S>> {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(BaseRequestHandler.class);
    
    private final T service;
    private final U validator;
    private final String basePath;
    
    public BaseRequestHandler(T service, U validator, String basePath) {
        this.service = service;
        this.validator = validator;
        this.basePath = basePath;
    }
    
    final void addRoutes(Javalin server) {
        server
        .get(basePath + "/{id}", ctx -> {
            LOG.info("Received GET request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            service.get(id).ifPresentOrElse(
                    job -> ctx.json(job).status(HTTP_OK), 
                    () -> ctx.status(HTTP_NOT_FOUND));
        })

        .post(basePath, ctx -> {
            LOG.info("Received POST request to: {}", ctx.fullUrl());
            S body = validator.validate(ctx).get();
            UUID generatedId = service.create(body);
            ctx.result(generatedId.toString()).status(HTTP_ACCEPTED);
        })

        .delete(basePath + "/{id}", ctx -> {
            LOG.info("Received DELETE request to: {}", ctx.fullUrl());
            UUID id = ctx.pathParamAsClass("{id}", UUID.class).get();
            service.delete(id);
            ctx.status(HTTP_NO_CONTENT);
        });
    }

}
