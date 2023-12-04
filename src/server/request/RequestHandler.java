package server.request;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.javalin.Javalin;

public class RequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final Properties properties;
    private final Javalin javalin;

    @Inject
    public RequestHandler(Properties properties) {
        this.properties = properties;
        javalin = Javalin.create()
                .get("/", context -> context.result("Hello World"));
    }
    
    public void start() {
        int port = Integer.parseInt(properties.getProperty("javalin.port"));
        javalin.start(port);
    }

}
