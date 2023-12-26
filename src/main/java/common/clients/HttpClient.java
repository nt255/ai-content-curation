package main.java.common.clients;

import static main.java.common.clients.HttpClient.RequestMethod.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    
    private static final Map<String, String> NO_HEADERS = Map.of();
    private static final JSONObject NO_BODY = new JSONObject();

    enum RequestMethod {
        GET, POST, PATCH, DELETE
    }
    
    public String get(String url) {
        return makeRequest(GET, url, NO_HEADERS, NO_BODY);
    }
    
    public String post(String url, Map<String, String> headers, JSONObject body) {
        return makeRequest(POST, url, headers, body);
    }
    
    public String post(String url, JSONObject body) {
        return post(url, NO_HEADERS, body);
    }
    
    public String delete(String url) {
        return makeRequest(DELETE, url, NO_HEADERS, NO_BODY);
    }

    private String makeRequest(RequestMethod requestMethod, String url, 
            Map<String, String> headers, JSONObject body) {
        try {
            LOG.info("Calling URL: {}", url);

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod(requestMethod.name());

            headers.entrySet().stream().forEach(
                    e -> connection.setRequestProperty(e.getKey(), e.getValue()));

            // request body
            if (RequestMethod.POST.equals(requestMethod) || 
                    RequestMethod.PATCH.equals(requestMethod)) {
                connection.setDoOutput(true);
                OutputStreamWriter writer = 
                        new OutputStreamWriter(connection.getOutputStream());
                writer.write(body.toString());
                writer.flush();
                writer.close();
            }

            // response
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            
            StringBuffer response = new StringBuffer();
            
            String line;
            while ((line = br.readLine()) != null)
                response.append(line);
            br.close();

            return response.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
