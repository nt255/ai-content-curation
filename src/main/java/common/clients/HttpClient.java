package main.java.common.clients;

import static java.net.HttpURLConnection.*;
import static main.java.common.clients.HttpClient.RequestMethod.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;
import lombok.Getter;

public class HttpClient {

    private static final Logger LOG = 
            LoggerFactory.getLogger(HttpClient.class);

    @Builder
    @Getter
    public static class Response {
        private final int code;
        private final String body;
    }

    enum RequestMethod {
        GET, POST, DELETE
    }


    public Response get(String url) {
        return makeRequest(GET, url, Map.of(), null);
    }

    public Response post(String url, 
            Map<String, String> headers, JSONObject body) {
        return makeRequest(POST, url, headers, body);
    }

    public Response post(String url, JSONObject body) {
        return makeRequest(POST, url, Map.of(), body);
    }

    public Response delete(String url) {
        return makeRequest(DELETE, url, Map.of(), null);
    }


    private Response makeRequest(RequestMethod method, String url, 
            Map<String, String> headers, JSONObject body) {

        try {
            LOG.info("Calling {} {}", method.name(), url);

            URL obj = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod(method.name());

            headers.entrySet().stream().forEach(
                    e -> conn.setRequestProperty(e.getKey(), e.getValue()));

            // ------ request body
            if (RequestMethod.POST.equals(method)) {
                LOG.info("with body {}", body.toString());

                conn.setDoOutput(true);
                OutputStreamWriter writer = 
                        new OutputStreamWriter(conn.getOutputStream());
                writer.write(body.toString());
                writer.flush();
                writer.close();
            }

            // ------ response
            InputStream inputStream;
            int code = conn.getResponseCode();
            if (code == HTTP_BAD_REQUEST || code == HTTP_NOT_FOUND)
                inputStream = conn.getErrorStream();
            else
                inputStream = conn.getInputStream();
            
            if (inputStream == null)
                return getResponse(code, "");
            
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);
            br.close();

            return getResponse(code, sb.toString());

        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Response getResponse(int code, String body) {
        Response response = Response.builder()
                .code(code)
                .body(body)
                .build();

        LOG.info("Received code: {}", response.getCode());
        if (body.isEmpty()) 
            LOG.info("empty body");
        else
            LOG.info("Received body: {}", body);

        return response;
    }
}
