package processors.clients;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import org.json.JSONArray;

import common.clients.HttpClient;
import common.clients.HttpClient.RequestMethod;

public class ChatGPTClient {

    private static final Logger LOG = LoggerFactory.getLogger(ChatGPTClient.class);

    @Inject private Properties properties;
    @Inject private HttpClient httpClient;

    public String makeRequest(String prompt) {
        return "dummy ChatGPT response";
//        String url = properties.getProperty("openai.url");
//        String secretkey = properties.getProperty("openai.secretkey");
//        String model = properties.getProperty("openai.model");
//
//        Map<String, String> headers = Map.of(
//                "Authorization", "Bearer " + secretkey,
//                "Content-Type", "application/json"
//                );
//
//        JSONObject body = new JSONObject()
//                .put("model", model)
//                .put("messages", new JSONArray(List.of(new JSONObject()
//                        .put("role", "user")
//                        .put("content", prompt))));
//
//        return httpClient.makeRequest(RequestMethod.POST, url, headers, body);
    }

}
