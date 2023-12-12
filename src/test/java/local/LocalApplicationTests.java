package test.java.local;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.enums.JobState;
import main.java.common.enums.JobType;
import main.java.common.file.FileServer;
import main.java.server.models.Job;
import test.java.TestWithInjections;

public class LocalApplicationTests extends TestWithInjections {

    private static final Logger LOG = LoggerFactory.getLogger(LocalApplicationTests.class);

    @Inject private HttpClient httpClient;
    @Inject private FileServer fileServer;
    @Inject private Properties properties;
    @Inject private Gson gson;


    @BeforeAll
    void startApplication() {
        // TODO: investigate why this works
        main.java.processor.Application.main(new String[]{"500"});
        main.java.server.Application.main(new String[0]);
    }


    private void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void serverAndProcessorTextFullFlowTest() {

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
        assertEquals(JobState.NEW, postResponse.getState());

        String id = postResponse.getId().toString();
        LOG.info("successfully created job with id: {}", id);


        // get
        String getUrl = jobsUrl + "/" + postResponse.getId();

        String getResponseString = httpClient.get(getUrl);
        Job getResponse = gson.fromJson(getResponseString, Job.class);

        assertEquals(postResponse.getId(), getResponse.getId());
        assertEquals(JobType.TEXT, getResponse.getType());
        assertEquals(JobState.NEW, getResponse.getState());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");

        assertNull(getResponse.getOutputText());
        assertNull(getResponse.getOutputImageFilename());
        assertNull(getResponse.getErrors());


        // submit
        String submitUrl = url + "/submit/" + getResponse.getId();
        httpClient.post(submitUrl);
        // TODO: check for submitted state as soon as this goes out


        sleep(5L);


        // make sure job is updated with results
        String getResponseStringTwo = httpClient.get(getUrl);
        Job getResponseTwo = gson.fromJson(getResponseStringTwo, Job.class);

        assertEquals(getResponse.getId(), getResponseTwo.getId());
        assertNotNull(getResponseTwo.getOutputText());


        // delete
        httpClient.delete(getUrl);
        assertThrows(
                RuntimeException.class, // TODO: handle 404s in httpClient
                () -> httpClient.get(getUrl));
    }

    @Test
    void pullingEveryMessagePushedTest() {

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

        int count = 3;
        Set<Job> jobs = IntStream.range(0, count).boxed().map(ignored -> {
            String postResponseString = httpClient.post(
                    jobsUrl, headers, body);
            Job postResponse = gson.fromJson(postResponseString, Job.class);

            assertEquals(postResponse.getCreatedOn(), 
                    postResponse.getLastModifiedOn());

            return postResponse;
        }).collect(Collectors.toSet());

        assertEquals(count, jobs.size());


        // submit
        jobs.stream().forEach(job -> {
            String submitUrl = url + "/submit/" + job.getId();
            httpClient.post(submitUrl);
        });


        sleep(5L);


        // get
        jobs.stream().forEach(job -> {
            String getUrl = jobsUrl + "/" + job.getId();

            String getResponseString = httpClient.get(getUrl);
            Job getResponse = gson.fromJson(getResponseString, Job.class);

            Instant lastModifiedOn = getResponse.getLastModifiedOn();
            Instant createdOn = getResponse.getCreatedOn();

            assertTrue(lastModifiedOn.isAfter(createdOn), 
                    "lastModified is not after createdOn");
        });
    }


    @Test
    void serverAndProcessorImageFullFlowTest() {

        // create
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String jobsUrl = url + "/jobs";
        Map<String, String> headers = Map.of();

        JSONObject body = new JSONObject()
                .put("type", "IMAGE")
                .put("state", "NEW")
                .put("parameters", new JSONObject()
                        .put("prompt", "chess grandmaster, intense, serious, suit")
                        .put("height", "16")
                        .put("width", "16")
                        .put("workflow", "fitnessAesthetics")
                        .put("checkpoint", "realDream_8Legendary.safetensors"));

        String postResponseString = httpClient.post(
                jobsUrl, headers, body);
        Job postResponse = gson.fromJson(postResponseString, Job.class);

        assertEquals(JobType.IMAGE, postResponse.getType());
        assertEquals(JobState.NEW, postResponse.getState());

        String id = postResponse.getId().toString();
        LOG.info("successfully created job with id: {}", id);


        // get
        String getUrl = jobsUrl + "/" + postResponse.getId();

        String getResponseString = httpClient.get(getUrl);
        Job getResponse = gson.fromJson(getResponseString, Job.class);

        assertEquals(postResponse.getId(), getResponse.getId());
        assertEquals(JobType.IMAGE, getResponse.getType());
        assertEquals(JobState.NEW, getResponse.getState());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");

        assertNull(getResponse.getOutputText());
        assertNull(getResponse.getOutputImageFilename());
        assertNull(getResponse.getErrors());


        // submit
        String submitUrl = url + "/submit/" + getResponse.getId();
        httpClient.post(submitUrl);
        // TODO: check for submitted state as soon as this goes out


        sleep(3L);


        // make sure job is updated with results
        String getResponseStringTwo = httpClient.get(getUrl);
        Job getResponseTwo = gson.fromJson(getResponseStringTwo, Job.class);

        assertEquals(getResponse.getId(), getResponseTwo.getId());
        assertNull(getResponseTwo.getOutputText());

        String outputFilename = getResponseTwo.getOutputImageFilename();
        assertNotNull(outputFilename);

        File outputFile = fileServer.downloadFile(outputFilename);
        assertTrue(outputFile.exists(), "file is not yet generated");


        // delete
        httpClient.delete(getUrl);
        assertThrows(
                RuntimeException.class, // TODO: handle 404s in httpClient
                () -> httpClient.get(getUrl));        
        assertTrue(!outputFile.exists(), "file is not yet deleted");
    }
}
