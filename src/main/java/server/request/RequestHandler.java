package main.java.server.request;

import com.google.inject.Inject;

import io.javalin.Javalin;

public class RequestHandler {

    private final TextRequestHandler textRequestHandler;
    private final ImageRequestHandler imageRequestHandler;

    @Inject
    public RequestHandler(
            TextRequestHandler textRequestHandler, 
            ImageRequestHandler imageRequestHandler) {
        
        this.textRequestHandler = textRequestHandler;
        this.imageRequestHandler = imageRequestHandler;
    }

    public void addRoutes(Javalin server) {
        textRequestHandler.addRoutes(server);
        imageRequestHandler.addRoutes(server);
    }

}
