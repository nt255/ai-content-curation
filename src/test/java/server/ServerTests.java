package test.java.server;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import test.java.TestWithInjections;

public class ServerTests extends TestWithInjections {

    @Inject private HttpClient httpClient;
    @Inject private Properties properties;

    @BeforeAll
    void startApplication() {
        main.java.server.Application.main(new String[0]);
    }
    
    @AfterAll
    void closeApplication() {
        main.java.server.Application.getApplication().close();
    }
    
    @Test
    void imageValidationTests() {
        
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String imagesUrl = url + "/images";

        JSONObject body = new JSONObject()
                .put("type", "IMAGE")
                .put("state", "COMPLETED")
                .put("params", new JSONArray());
        
        assertThrows(
                RuntimeException.class,
                () ->  httpClient.post(imagesUrl, body));
    }

}
