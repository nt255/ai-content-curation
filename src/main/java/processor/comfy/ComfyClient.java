package main.java.processor.comfy;

import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.processor.comfy.ComfyWorkflow.AlternativeWorkflowBuilder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class ComfyClient {

    private static final Logger LOG = LoggerFactory.getLogger(ComfyClient.class);

    private static final String WF_FORMATTED_STRING = 
            "src/main/resources/workflows/%s_workflow.json";

    @Inject private HttpClient httpClient;

    private String promptUrl;
    private String historyUrl;

    private ComfyWorkflow loadedWorkflow;


    @Inject
    public ComfyClient(Properties properties) {
        String baseUrl = properties.getProperty("comfy.server.address");
        this.promptUrl = baseUrl + "/prompt";
        this.historyUrl = baseUrl + "/history";
    }

    public void loadWorkflow(String wfname, Map<String, String> params) {
        String baseWfFile = String.format(WF_FORMATTED_STRING, wfname);
        loadedWorkflow = new AlternativeWorkflowBuilder()
                .setBaseWorkflowFile(baseWfFile)
                .setParams(params)
                .build();
        loadedWorkflow.generateNewSeed();
    }

    /**
     * NOTE The methods below will NOT return anything. They will make a call to the
     * localhost that hosts your instance of ComfyUI, and the output images will be
     * saved to a directory, which by default is [comfyUi's
     * drive]://comfyclient_output/
     **/

    // will load in default options when queued without arguments
    // currently simply prints out the error, but should be updated to update
    // relevant JobResponse
    public void queuePrompt() throws IllegalStateException {

        LOG.info("A prompt was queued with no arguments. Proceeding...");
        LOG.info("Verifying that workflow has been loaded...");

        if (loadedWorkflow != null) {
            LOG.info("Workflow has been verified.");
            LOG.info("Deleting history.");
            clearQueueHistory();
            sendTask();
        } else {
            throw new IllegalStateException(
                    "Workflow has not been loaded correctly (it is null)! "
                            + "Check to see that workflow names are properly spelled.");
        }
    }

    public void upscaleImages(String[] imageIds) {
        throw new UnsupportedOperationException();
    }

    private void sendTask() {
        try {
            loadedWorkflow.generateNewSeed();
            JSONObject request = new JSONObject()
                    .put("prompt", loadedWorkflow.getJson());
            LOG.info("sending over to comfy {}", request);

            String responseHistory = httpClient.get(historyUrl);

            HttpClient httpClient = new HttpClient();
            String response = httpClient.post(promptUrl, request);

            LOG.info("Response from server: " + response);
            LOG.info(responseHistory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearQueueHistory() {
        LOG.info("Sent request to clear history.");
        httpClient.post(historyUrl, new JSONObject()
                .put("clear", true));
    }
}
