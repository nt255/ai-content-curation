package test.java.server;

import static java.net.HttpURLConnection.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.clients.HttpClient.Response;
import test.java.TestWithInjections;

public class ServerTests extends TestWithInjections {

    @Inject private HttpClient httpClient;
    @Inject private Properties properties;

    private String port;
    private String url;
    private String imagesUrl;

    @BeforeAll
    void startApplication() {
        port = properties.getProperty("javalin.port");
        url = "http://localhost:" + port;
        imagesUrl = url + "/images";

        main.java.server.Application.main(new String[0]);
    }

    @AfterAll
    void closeApplication() {
        main.java.server.Application.getApplication().close();
    }

    @Test
    void notFoundResponseTest() {
        String getUrl = imagesUrl + "/" + UUID.randomUUID().toString();

        Response response =  httpClient.get(getUrl);
        assertEquals(HTTP_NOT_FOUND, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");
    }

    @Test
    void noParametersProvidedTest() {
        JSONObject body = new JSONObject()
                .put("type", "IMAGE")
                .put("state", "COMPLETED")
                .put("params", new JSONArray());

        Response response = httpClient.post(imagesUrl, body);
        JSONObject responseBody = new JSONObject(response.getBody());
        JSONArray errors = responseBody.getJSONArray("errors");

        assertEquals(HTTP_BAD_REQUEST, response.getCode());

        assertEquals(1, errors.length());
        assertEquals("NO_PARAMS", errors.getJSONObject(0).get("code"));
        assertEquals("no parameters are provided", 
                errors.getJSONObject(0).get("detail"));
    }

    @Test
    void multipleErrorsTest() {
        JSONObject body = new JSONObject()
                .put("type", "IMAGE")
                .put("state", "COMPLETED")
                .put("params", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "UPSCALE"))
                        .put(new JSONObject()
                                .put("type", "CREATE")));

        Response response = httpClient.post(imagesUrl, body);
        JSONObject responseBody = new JSONObject(response.getBody());
        JSONArray errors = responseBody.getJSONArray("errors");

        assertEquals(HTTP_BAD_REQUEST, response.getCode());
        assertEquals(2, errors.length());
        
        assertEquals("UPSCALE_IMAGE_MISSING", errors.getJSONObject(0).get("code"));
        assertEquals("upscale must work on an existing image", 
                errors.getJSONObject(0).get("detail"));
        
        assertEquals("CREATE_IS_NOT_FIRST", errors.getJSONObject(1).get("code"));
        assertEquals("create must appear first", 
                errors.getJSONObject(1).get("detail"));
    }

}
