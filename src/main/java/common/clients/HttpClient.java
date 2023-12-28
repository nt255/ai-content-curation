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

    enum RequestMethod {
        GET, POST, DELETE
    }
    
    public String get(String url) {
        return makeRequest(GET, url, Map.of(), null);
    }
    
    public String post(String url, Map<String, String> headers, JSONObject body) {
        return makeRequest(POST, url, headers, body);
    }
    
    public String post(String url, JSONObject body) {
        return makeRequest(POST, url, Map.of(), body);
    }
    
    public String delete(String url) {
        return makeRequest(DELETE, url, Map.of(), null);
    }

    private String makeRequest(RequestMethod method, String url, 
            Map<String, String> headers, JSONObject body) {
        
        try {
            LOG.info("Calling {} {}", method.name(), url);

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod(method.name());

            headers.entrySet().stream().forEach(
                    e -> connection.setRequestProperty(e.getKey(), e.getValue()));

            // request body
            if (RequestMethod.POST.equals(method)) {
                LOG.info("with body {}", body.toString());
                
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
