package main.java.processor.comfy;

import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.common.models.image.ImageParams;
import main.java.processor.comfy.ComfyWorkflow.ComfyWorkflowBuilder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ComfyClient {

    private static final Logger LOG = LoggerFactory.getLogger(ComfyClient.class);

    private static final String WF_FORMATTED_STRING = 
            "src/main/resources/workflows/%s_workflow.json";
    
    private static final String UPSCALER_WF_PREFIX = "upscaler";

    private final HttpClient httpClient;

    private final String workingDirectory;
    private final String promptUrl;
    private final String historyUrl;

    private ComfyWorkflow workflow;


    @Inject
    public ComfyClient(Properties properties, HttpClient httpClient) {
        this.httpClient = httpClient;
        this.workingDirectory = properties.getProperty("comfy.working.directory");

        String baseUrl = properties.getProperty("comfy.server.address");
        this.promptUrl = baseUrl + "/prompt";
        this.historyUrl = baseUrl + "/history";

        checkConnection();
    }
    
    private void loadWorkflow(ImageParams params, String workflowPrefix) {
        String baseWfFile = String.format(WF_FORMATTED_STRING, workflowPrefix);
        workflow = new ComfyWorkflowBuilder()
                .setBaseWorkflowFile(baseWfFile)
                .setParams(params)
                .setOutputDirectory(workingDirectory) // must always be set
                .build();
    }

    public void loadWorkflow(ImageParams params) {
        loadWorkflow(params, params.getWorkflow());
    }
    
    public void loadUpscalerWorkflow(ImageParams params) {
        loadWorkflow(params, UPSCALER_WF_PREFIX);
    }

    /**
     * NOTE The methods below will NOT return anything. They will make a call to the
     * localhost that hosts your instance of ComfyUI, and the output images will be
     * saved to a directory, which by default is [comfyUi's
     * drive]://comfyclient_output/
     **/
    public void queuePrompt() {
        if (workflow == null)
            throw new IllegalStateException(
                    "Workflow has not been loaded correctly (it is null)! "
                            + "Check to see that workflow names are properly spelled.");
        
        LOG.info("Deleting history.");
        clearQueueHistory();
        sendTask();
    }

    private void sendTask() {
        try {
            JSONObject request = new JSONObject()
                    .put("prompt", workflow.getJson());

            LOG.info("sending prompt: {}", request);

            JSONObject response = new JSONObject(
                    httpClient.post(promptUrl, request));

            LOG.info("received back: {}", response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkConnection() {
        LOG.info("Checking connection to Comfy.");
        try {
            httpClient.get(historyUrl);
            LOG.info("Succesfully connected to Comfy.");
        } catch (RuntimeException e) {
            LOG.error("Unable to connect to Comfy.");
            e.printStackTrace();
        }
    }

    private void clearQueueHistory() {
        httpClient.post(historyUrl, new JSONObject()
                .put("clear", true));
    }
}
