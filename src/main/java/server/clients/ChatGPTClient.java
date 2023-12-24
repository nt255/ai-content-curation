package main.java.server.clients;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import com.google.inject.Inject;

import main.java.common.clients.HttpClient;

import org.json.JSONArray;


public class ChatGPTClient {

    @Inject private Properties properties;
    @Inject private HttpClient httpClient;

    public String generate(String prompt) {
        String url = properties.getProperty("openai.url");
        String secretkey = properties.getProperty("openai.secretkey");
        String model = properties.getProperty("openai.model");

        Map<String, String> headers = Map.of(
                "Authorization", "Bearer " + secretkey,
                "Content-Type", "application/json"
                );

        JSONObject body = new JSONObject()
                .put("model", model)
                .put("messages", new JSONArray(List.of(new JSONObject()
                        .put("role", "user")
                        .put("content", prompt))));

        return extractMessageFromJSONResponse(
                httpClient.post(url, headers, body));
    }

    private String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 11;
        int end = response.indexOf("\"", start);

        return response.substring(start, end);
    }

}
