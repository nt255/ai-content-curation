package common.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.json.JSONObject;

public class HttpClient {

    public enum RequestMethod {
        GET, POST, PATCH, DELETE
    }

    public String makeRequest(RequestMethod requestMethod, String url,	
            Map<String, String> headers, JSONObject body) {

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod(requestMethod.name());

            headers.entrySet().stream()
            .forEach(e -> connection.setRequestProperty(e.getKey(), e.getValue()));

            // request body
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body.toString());
            writer.flush();
            writer.close();

            // response
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // calls the method to extract the message.
            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 11;
        int end = response.indexOf("\"", start);

        return response.substring(start, end);
    }

}
