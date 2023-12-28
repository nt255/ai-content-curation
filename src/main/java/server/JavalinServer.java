package main.java.server;

import java.lang.reflect.Type;
import java.util.Properties;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import io.javalin.validation.JavalinValidation;
import main.java.server.request.RequestHandler;

class JavalinServer {

    private final Javalin javalin;

    @Inject
    public JavalinServer(Gson gson, Properties properties,
            RequestHandler requestHandler) {
        
        JavalinValidation.register(UUID.class, s -> UUID.fromString(s));

        this.javalin = Javalin.create(
                config -> config.jsonMapper(getGsonMapper(gson)));
        requestHandler.addRoutes(javalin);
        
        int port = Integer.parseInt(
                properties.getProperty("javalin.port"));
        javalin.start(port);
    }
    
    void close() {
        javalin.close();
    }
    
    private JsonMapper getGsonMapper(Gson gson) {
        return new JsonMapper() {
            @Override
            public String toJsonString(
                    @NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }
            @Override
            public <T> T fromJsonString(
                    @NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };
    }

}
