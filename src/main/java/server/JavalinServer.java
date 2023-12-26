package main.java.server;

import java.util.Properties;
import java.util.UUID;

import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.validation.JavalinValidation;
import main.java.server.request.RequestHandler;

class JavalinServer {
    
    private final Javalin javalin;
    private final int port;
    
    @Inject
    public JavalinServer(Properties properties, RequestHandler requestHandler) {
        this.javalin = Javalin.create();
        this.port = Integer.parseInt(properties.getProperty("javalin.port"));
        
        JavalinValidation.register(UUID.class, s -> UUID.fromString(s));
        
        requestHandler.addRoutes(javalin);
    }
    
    void start() {
        javalin.start(port);
    }

}
