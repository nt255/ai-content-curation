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
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.file.FileServer;
import main.java.common.models.JobState;
import main.java.common.models.TextParams;
import main.java.server.models.Image;
import main.java.server.models.Text;
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
        Map<String, String> headers = Map.of();

        JSONObject body = new JSONObject()
                .put("type", "TEXT")
                .put("state", "COMPLETED")
                .put("params", new JSONObject()
                        .put("prompt", "Write me a nice story about a farmer.")
                        .put("numTokens", 2));

        String postResponseString = httpClient.post(
                textsUrl, headers, body);
        Text postResponse = gson.fromJson(postResponseString, Text.class);

        assertEquals(JobState.NEW, postResponse.getState());
        assertNotNull(postResponse.getParams());
        
        TextParams params = postResponse.getParams();
        assertEquals("Write me a nice story about a farmer.", params.getPrompt());
        assertEquals(2, params.getNumTokens());


        // -----get-----
        String getUrl = textsUrl + "/" + postResponse.getId();

        String getResponseString = httpClient.get(getUrl);
        Text getResponse = gson.fromJson(getResponseString, Text.class);

        assertEquals(postResponse.getId(), getResponse.getId());
        assertEquals(JobState.NEW, getResponse.getState());
        assertNotNull(getResponse.getParams());
        assertNotNull(getResponse.getParams().getPrompt());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");

        assertNull(getResponse.getOutputText());
        assertNull(getResponse.getErrors());


        
        sleep(20L);


        // -----make sure text is updated with results-----
        String getResponseStringTwo = httpClient.get(getUrl);
        Text getResponseTwo = gson.fromJson(getResponseStringTwo, Text.class);

        assertEquals(getResponse.getId(), getResponseTwo.getId());
        assertEquals(JobState.COMPLETED, getResponseTwo.getState());
        assertNotNull(getResponseTwo.getOutputText());


        // -----delete-----
        httpClient.delete(getUrl);
        assertThrows(
                RuntimeException.class, // TODO: handle 404s in httpClient
                () -> httpClient.get(getUrl));
    }

    @Test
    void pullingEveryMessagePushed() {

        // -----create-----
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String textsUrl = url + "/texts";
        Map<String, String> headers = Map.of();

        JSONObject body = new JSONObject()
                .put("type", "TEXT")
                .put("state", "COMPLETED")
                .put("params", new JSONObject()
                        .put("prompt", "Write me a nice story about a farmer.")
                        .put("numTokens", 2));

        int count = 3;
        Set<Text> texts = IntStream.range(0, count).boxed().map(ignored -> {
            String postResponseString = httpClient.post(
                    textsUrl, headers, body);
            Text postResponse = gson.fromJson(postResponseString, Text.class);

            assertEquals(postResponse.getCreatedOn(), 
                    postResponse.getLastModifiedOn());

            return postResponse;
        }).collect(Collectors.toSet());

        assertEquals(count, texts.size());



        sleep(20L);


        // -----get-----
        texts.stream().forEach(text -> {
            String getUrl = textsUrl + "/" + text.getId();

            String getResponseString = httpClient.get(getUrl);
            Text getResponse = gson.fromJson(getResponseString, Text.class);

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
        Map<String, String> headers = Map.of();

        JSONObject body = new JSONObject()
                .put("type", "IMAGE")
                .put("state", "NEW")
                .put("params", new JSONObject()
                        .put("prompt", "Write me a nice story about a farmer.")
                        .put("height", height.toString())
                        .put("width", width.toString())
                        .put("workflow", "fitnessAesthetics")
                        .put("checkpoint", "realDream_8Legendary.safetensors"));

        String postResponseString = httpClient.post(
                imagesUrl, headers, body);
        Image postResponse = gson.fromJson(postResponseString, Image.class);

        assertEquals(JobState.NEW, postResponse.getState());
        assertNotNull(postResponse.getParams());


        // -----get-----
        String getUrl = imagesUrl + "/" + postResponse.getId();

        String getResponseString = httpClient.get(getUrl);
        Image getResponse = gson.fromJson(getResponseString, Image.class);

        assertEquals(postResponse.getId(), getResponse.getId());
        assertEquals(JobState.NEW, getResponse.getState());
        assertNotNull(getResponse.getParams());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");

        assertNull(getResponse.getOutputFilename());
        assertNull(getResponse.getErrors());



        sleep(20L);


        // -----make sure image is updated with results-----
        String getResponseStringTwo = httpClient.get(getUrl);
        Image getResponseTwo = gson.fromJson(getResponseStringTwo, Image.class);

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
                RuntimeException.class, // TODO: handle 404s in httpClient
                () -> httpClient.get(getUrl));        
        assertTrue(!outputFile.exists(), "file is not yet deleted");
        assertThrows(
                FileNotFoundException.class,
                () -> fileServer.downloadFile(outputFilename));     
    }
}
