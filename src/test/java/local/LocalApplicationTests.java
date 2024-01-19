package test.java.local;

import static java.net.HttpURLConnection.*;

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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.clients.HttpClient.Response;
import main.java.common.file.FileServer;
import main.java.common.models.JobState;
import main.java.server.models.image.GetImageResponse;
import main.java.server.models.post.GetPostResponse;
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
        assertNotNull(getResponse.getParams());
        assertNotNull(getResponse.getParams().get(0).getPrompt());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");
        assertNull(getResponse.getOutputText());



        sleep(30L);


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
            Response response = httpClient.post(textsUrl, body);
            assertEquals(HTTP_ACCEPTED, response.getCode());
            
            return response.getBody();
        }).collect(Collectors.toSet());

        assertEquals(count, generatedIds.size());



        sleep(30L);


        // -----get-----
        generatedIds.stream().forEach(generatedId -> {
            String getUrl = textsUrl + "/" + generatedId;

            Response response = httpClient.get(getUrl);
            assertEquals(HTTP_OK, response.getCode());
            
            String getResponseString = response.getBody();
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
        
        String localOutputDirectory = 
                properties.getProperty("local.output.directory");

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

        Response response = httpClient.post(imagesUrl, body);
        assertEquals(HTTP_ACCEPTED, response.getCode());
        
        String generatedId = response.getBody();


        // -----get-----
        String getUrl = imagesUrl + "/" + generatedId;

        response = httpClient.get(getUrl);
        assertEquals(HTTP_OK, response.getCode());
        
        String getResponseString = response.getBody();
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



        sleep(30L);


        // -----make sure image is updated with results-----
        response = httpClient.get(getUrl);
        assertEquals(HTTP_OK, response.getCode());
        
        String getResponseStringTwo = response.getBody();
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
            outputFile = fileServer.downloadFile(
                    outputFilename, localOutputDirectory);
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
        String localDirectory = properties.getProperty("comfy.working.directory");
        Path directory = new File(localDirectory).toPath();
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
            assertTrue(!stream.iterator().hasNext(), "local directory not empty");
        } catch (IOException e) {
            fail("unable to open processor local directory");
        }


        // -----delete-----
        response = httpClient.delete(getUrl);
        assertEquals(HTTP_NO_CONTENT, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");
        
        response = httpClient.get(getUrl);
        assertEquals(HTTP_NOT_FOUND, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");
          
        assertThrows(
                FileNotFoundException.class,
                () -> fileServer.downloadFile(
                        outputFilename, localOutputDirectory));     
    }
    
    
    @Test
    void postFullFlowTest() {
        // -----create-----
        String port = properties.getProperty("javalin.port");
        String url = "http://localhost:" + port;
        String postsUrl = url + "/posts";

        String uuidString = UUID.randomUUID().toString();
        JSONObject body = new JSONObject()
                .put("textJobId", uuidString)
                .put("imageJobId", uuidString);

        Response response = httpClient.post(postsUrl, body);
        assertEquals(HTTP_ACCEPTED, response.getCode());
        
        String generatedId = response.getBody();


        // -----get-----
        String getUrl = postsUrl + "/" + generatedId;

        response =  httpClient.get(getUrl);
        assertEquals(HTTP_OK, response.getCode());
        
        GetPostResponse getResponse = gson.fromJson(
                response.getBody(), GetPostResponse.class);

        assertEquals(generatedId, getResponse.getId().toString());
        
        assertEquals(uuidString, getResponse.getTextJobId().toString());
        assertEquals(uuidString, getResponse.getImageJobId().toString());

        assertTrue(getResponse.getCreatedOn().isBefore(Instant.now()), 
                "createdOn is not before current time");
        assertTrue(getResponse.getLastModifiedOn().isBefore(Instant.now()), 
                "lastModified is not before current time");


        // -----delete-----
        response = httpClient.delete(getUrl);
        assertEquals(HTTP_NO_CONTENT, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");
        
        response = httpClient.get(getUrl);
        assertEquals(HTTP_NOT_FOUND, response.getCode());
        assertTrue(response.getBody().isEmpty(), "body not empty");
    }
    
}
