package test.java.server;

import static java.net.HttpURLConnection.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.clients.HttpClient.Response;
import main.java.common.models.JobState;
import main.java.server.models.text.GetTextResponse;
import test.java.TestWithInjections;

public class ServerTests extends TestWithInjections {

    @Inject private HttpClient httpClient;
    @Inject private Properties properties;
    @Inject private Gson gson;

    private String port;
    private String url;
    private String textsUrl;
    private String imagesUrl;

    @BeforeAll
    void startApplication() {
        port = properties.getProperty("javalin.port");
        url = "http://localhost:" + port;

        textsUrl = url + "/texts";
        imagesUrl = url + "/images";

        main.java.server.Application.main(new String[0]);
    }

    @AfterAll
    void closeApplication() {
        main.java.server.Application.getApplication().close();
    }

    private void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void textNotFoundResponseTest() {
        String getUrl = textsUrl + "/" + UUID.randomUUID().toString();

        Response response =  httpClient.get(getUrl);
        assertEquals(HTTP_NOT_FOUND, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");
    }
    
    @Test
    void multipleStepsNotSupportedTest() {
        JSONObject body = new JSONObject()
                .put("type", "TEXT")
                .put("state", "COMPLETED")
                .put("params", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "CREATE"))
                        .put(new JSONObject()
                                .put("type", "CREATE_HASHTAGS")));

        Response response = httpClient.post(textsUrl, body);
        JSONObject responseBody = new JSONObject(response.getBody());
        JSONArray errors = responseBody.getJSONArray("errors");

        assertEquals(HTTP_BAD_REQUEST, response.getCode());
        assertEquals(1, errors.length());

        assertEquals("MULTIPLE_STEPS_NOT_SUPPORTED", 
                errors.getJSONObject(0).get("code"));
        assertEquals("multiple steps are not supported", 
                errors.getJSONObject(0).get("detail"));
    }

    @Test
    void imageNotFoundResponseTest() {
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
        assertEquals("create image must appear first", 
                errors.getJSONObject(1).get("detail"));
    }

    @Test
    void serviceCallTextGenerationTest() {
        JSONObject body = new JSONObject()
                .put("type", "TEXT")
                .put("state", "COMPLETED")
                .put("params", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "CREATE")
                                .put("prompt", "Write me a short sentence.")))
                .put("isServiceCall", true);

        Response response = httpClient.post(textsUrl, body);
        assertEquals(HTTP_ACCEPTED, response.getCode());

        String generatedId = response.getBody();


        // -----get-----
        String getUrl = textsUrl + "/" + generatedId;

        response =  httpClient.get(getUrl);
        assertEquals(HTTP_OK, response.getCode());

        String getResponseString = response.getBody();
        GetTextResponse getResponse = gson.fromJson(
                getResponseString, GetTextResponse.class);

        assertEquals(generatedId, getResponse.getId().toString());
        assertEquals(JobState.SUBMITTED, getResponse.getState());
        

        sleep(5L);
        

        // -----make sure text is updated with results-----
        response =  httpClient.get(getUrl);
        assertEquals(HTTP_OK, response.getCode());

        String getResponseStringTwo = response.getBody();
        GetTextResponse getResponseTwo = gson.fromJson(
                getResponseStringTwo, GetTextResponse.class);

        assertEquals(getResponse.getId(), getResponseTwo.getId());
        assertEquals(JobState.COMPLETED, getResponseTwo.getState());
        assertNotNull(getResponseTwo.getOutputText());


        // -----delete-----
        response = httpClient.delete(getUrl);
        assertEquals(HTTP_NO_CONTENT, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");

        response = httpClient.get(getUrl);
        assertEquals(HTTP_NOT_FOUND, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");
    }



}
