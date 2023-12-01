package processors.clients;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;

import common.PropertiesReader;
import common.clients.HttpClient;
import common.clients.HttpClient.RequestMethod;

public class ChatGPTClient {

	private PropertiesReader properties = PropertiesReader.getInstance();
	private HttpClient httpClient;

	public ChatGPTClient() {
		httpClient = new HttpClient();
	}

	public String makeRequest(String prompt) {
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

		return httpClient.makeRequest(RequestMethod.POST, url, headers, body);
	}


}
