package main.java.common.clients;

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

    private enum RequestMethod {
        GET, POST, PATCH, DELETE
    }
    
    public String get(String url) {
        return makeRequest(RequestMethod.GET, url, Map.of(), new JSONObject());
    }
    
    public String post(String url, Map<String, String> headers, JSONObject body) {
        return makeRequest(RequestMethod.POST, url, headers, body);
    }
    
    public String post(String url, JSONObject body) {
        return post(url, Map.of(), body);
    }
    
    public String delete(String url) {
        return makeRequest(RequestMethod.DELETE, url, Map.of(), new JSONObject());
    }

    private String makeRequest(RequestMethod requestMethod, String url, Map<String, String> headers, JSONObject body) {
        try {
            LOG.info("Calling URL: {}", url);

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod(requestMethod.name());

            headers.entrySet().stream().forEach(e -> connection.setRequestProperty(e.getKey(), e.getValue()));

            // request body
            if (RequestMethod.POST.equals(requestMethod) || 
                    RequestMethod.PATCH.equals(requestMethod)) {
                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body.toString());
                writer.flush();
                writer.close();
            }

            // response
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null)
                response.append(line);
            br.close();

            return response.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
