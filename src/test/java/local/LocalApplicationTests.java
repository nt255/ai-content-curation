package test.java.local;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.enums.JobState;
import main.java.common.enums.JobType;
import main.java.server.models.Job;
import test.java.TestWithInjections;

public class LocalApplicationTests extends TestWithInjections {

    private static final Logger LOG = LoggerFactory.getLogger(LocalApplicationTests.class);

    @Inject private HttpClient httpClient;
    @Inject private Properties properties;
    @Inject private Gson gson;
    

    @BeforeEach
    void startApplication() {
        CompletableFuture<Void> processorWithTimeout =
                CompletableFuture.runAsync(() -> {
                    main.java.processor.Application.main(new String[]{"500"});
                });

        CompletableFuture<Void> javalin =
                CompletableFuture.runAsync(() -> {
                    main.java.server.Application.main(new String[0]);
                });

        CompletableFuture.allOf(
                processorWithTimeout, javalin).toCompletableFuture().join();
    }

    @Test
    void serverAndProcessorFullFlowTest() {

        // create
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String jobsUrl = url + "/jobs";
        Map<String, String> headers = Map.of();

        JSONObject body = new JSONObject()
                .put("type", "TEXT")
                .put("state", "COMPLETED")
                .put("parameters", new JSONObject()
                        .put("prompt", "Write me a nice story about a farmer."));

        String postResponseString = httpClient.post(
                jobsUrl, headers, body);
        Job postResponse = gson.fromJson(postResponseString, Job.class);

        assertEquals(JobType.TEXT, postResponse.getType());
        assertEquals(JobState.WAITING, postResponse.getState());

        String id = postResponse.getId().toString();
        LOG.info("successfully created job with id: {}", id);


        // get
        String getUrl = jobsUrl + "/" + postResponse.getId();

        String getResponseString = httpClient.get(getUrl);
        Job getResponse = gson.fromJson(getResponseString, Job.class);

        assertEquals(postResponse.getId(), getResponse.getId());
        assertEquals(JobType.TEXT, getResponse.getType());
        assertEquals(JobState.WAITING, getResponse.getState());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");

        assertNull(getResponse.getTextResult());
        assertNull(getResponse.getImageResult());
        assertNull(getResponse.getProcessingNotes());


        // submit
        String submitUrl = url + "/submit/" + getResponse.getId();
        httpClient.post(submitUrl, headers, new JSONObject());


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // make sure job is updated with results
        String getResponseStringTwo = httpClient.get(getUrl);
        Job getResponseTwo = gson.fromJson(getResponseStringTwo, Job.class);

        assertEquals(getResponse.getId(), getResponseTwo.getId());
        assertNotNull(getResponseTwo.getTextResult());


        // delete
        httpClient.delete(getUrl);
        assertThrows(
                RuntimeException.class, // TODO: handle 404s in httpClient
                () -> httpClient.get(getUrl));
    }



}
