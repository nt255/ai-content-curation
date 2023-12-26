package test.java.local;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.file.FileServer;
import main.java.common.models.JobState;
import main.java.server.models.image.GetImageResponse;
import main.java.server.models.text.GetTextResponse;
import test.java.TestWithInjections;


public class LocalApplicationTests extends TestWithInjections {

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
    void serverAndProcessorTextFullFlow() {

        // -----create-----
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String textsUrl = url + "/texts";

        JSONObject body = new JSONObject()
                .put("type", "TEXT")
                .put("state", "COMPLETED")
                .put("params", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "CREATE")
                                .put("prompt", "Write me a nice story about a farmer.")
                                .put("numTokens", 2)));

        String generatedId = httpClient.post(textsUrl, body);


        // -----get-----
        String getUrl = textsUrl + "/" + generatedId;

        String getResponseString = httpClient.get(getUrl);
        GetTextResponse getResponse = gson.fromJson(
                getResponseString, GetTextResponse.class);

        assertEquals(generatedId, getResponse.getId().toString());
        assertEquals(JobState.SUBMITTED, getResponse.getState());
        assertNotNull(getResponse.getParams());
        assertNotNull(getResponse.getParams().get(0).getPrompt());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");
        assertNull(getResponse.getOutputText());



        sleep(10L);


        // -----make sure text is updated with results-----
        String getResponseStringTwo = httpClient.get(getUrl);
        GetTextResponse getResponseTwo = gson.fromJson(
                getResponseStringTwo, GetTextResponse.class);

        assertEquals(getResponse.getId(), getResponseTwo.getId());
        assertEquals(JobState.COMPLETED, getResponseTwo.getState());
        assertNotNull(getResponseTwo.getOutputText());


        // -----delete-----
        httpClient.delete(getUrl);
        assertThrows(
                RuntimeException.class,
                () -> httpClient.get(getUrl));
    }

    @Test
    void pullingEveryMessagePushed() {

        // -----create-----
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String textsUrl = url + "/texts";

        JSONObject body = new JSONObject()
                .put("type", "TEXT")
                .put("state", "COMPLETED")
                .put("params", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "CREATE")
                                .put("prompt", "Write me a nice story about a farmer.")
                                .put("numTokens", 2)));

        int count = 3;
        Set<String> generatedIds = IntStream.range(0, count).boxed().map(ignored -> {
            return httpClient.post(textsUrl, body);
        }).collect(Collectors.toSet());

        assertEquals(count, generatedIds.size());



        sleep(10L);


        // -----get-----
        generatedIds.stream().forEach(generatedId -> {
            String getUrl = textsUrl + "/" + generatedId;

            String getResponseString = httpClient.get(getUrl);
            GetTextResponse getResponse = gson.fromJson(
                    getResponseString, GetTextResponse.class);

            Instant lastModifiedOn = getResponse.getLastModifiedOn();
            Instant createdOn = getResponse.getCreatedOn();

            assertTrue(lastModifiedOn.isAfter(createdOn), 
                    "lastModified is not after createdOn");
        });
    }


    @RepeatedTest(1)
    void serverAndProcessorImageFullFlow() {

        Integer height = 16;
        Integer width = 16;

        // -----create-----
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String imagesUrl = url + "/images";

        JSONObject body = new JSONObject()
                .put("type", "IMAGE")
                .put("state", "NEW")
                .put("params", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "CREATE")
                                .put("prompt", "Write me a nice story about a farmer.")
                                .put("height", height.toString())
                                .put("width", width.toString())
                                .put("workflow", "fitnessAesthetics")
                                .put("checkpoint", "realDream_8Legendary.safetensors")));

        String generatedId = httpClient.post(imagesUrl, body);


        // -----get-----
        String getUrl = imagesUrl + "/" + generatedId;

        String getResponseString = httpClient.get(getUrl);
        GetImageResponse getResponse = gson.fromJson(
                getResponseString, GetImageResponse.class);

        assertEquals(generatedId, getResponse.getId().toString());
        assertEquals(JobState.SUBMITTED, getResponse.getState());
        assertNotNull(getResponse.getParams());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");

        assertNull(getResponse.getOutputFilename());



        sleep(10L);


        // -----make sure image is updated with results-----
        String getResponseStringTwo = httpClient.get(getUrl);
        GetImageResponse getResponseTwo = gson.fromJson(
                getResponseStringTwo, GetImageResponse.class);

        assertEquals(getResponse.getId(), getResponseTwo.getId());
        assertEquals(JobState.COMPLETED, getResponseTwo.getState());

        String outputFilename = getResponseTwo.getOutputFilename();
        assertNotNull(outputFilename);

        // -----test format of filename-----
        String[] split = outputFilename.split("\\.");
        try {
            UUID.fromString(split[0]);
        } catch (IllegalArgumentException e){
            fail("file name is not a valid UUID");
        }
        Set<String> supportedExts = Set.of("jpg", "png");
        assertTrue(supportedExts.contains(split[1]), "extension not supported");

        // -----make sure file exists-----
        File outputFile = null;
        try {
            outputFile = fileServer.downloadFile(outputFilename);
        } catch (FileNotFoundException e) {
            fail("unable to download file");
        }
        assertNotNull(outputFile);
        assertTrue(outputFile.exists(), "file is not yet generated");

        // -----make sure image has correct dimensions-----
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(outputFile);
        } catch (IOException e) {
            fail("unable to read as image");
        }
        assertEquals(height, bufferedImage.getHeight());
        assertEquals(width, bufferedImage.getWidth());

        // -----processor local directory should be cleared-----
        String localDirectory = properties.getProperty("comfy.output.directory");
        Path directory = new File(localDirectory).toPath();
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
            assertTrue(!stream.iterator().hasNext(), "local directory not empty");
        } catch (IOException e) {
            fail("unable to open processor local directory");
        }


        // -----delete-----
        httpClient.delete(getUrl);
        assertThrows(
                RuntimeException.class,
                () -> httpClient.get(getUrl));        
        assertTrue(!outputFile.exists(), "file is not yet deleted");
        assertThrows(
                FileNotFoundException.class,
                () -> fileServer.downloadFile(outputFilename));     
    }
}
