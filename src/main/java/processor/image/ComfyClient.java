package main.java.processor.image;

import com.google.inject.Inject;

import main.java.common.clients.HttpClient;
import main.java.processor.image.ComfyWorkflow.ComfyWorkflowBuilder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class ComfyClient {

    private static final Logger LOG = LoggerFactory.getLogger(ComfyClient.class);

    private static final String WF_FORMATTED_STRING = 
            "src/main/resources/workflows/%s_workflow.json";

    private HttpClient httpClient;

    private String outputDirectory;
    private String promptUrl;
    private String historyUrl;

    private ComfyWorkflow workflow;


    @Inject
    public ComfyClient(Properties properties, HttpClient httpClient) {
        this.httpClient = httpClient;
        this.outputDirectory = properties.getProperty("comfy.output.directory");
        
        String baseUrl = properties.getProperty("comfy.server.address");
        this.promptUrl = baseUrl + "/prompt";
        this.historyUrl = baseUrl + "/history";
        
        checkConnection();
    }

    public void loadWorkflow(String wfname, Map<String, String> params) {
        String baseWfFile = String.format(WF_FORMATTED_STRING, wfname);
        workflow = new ComfyWorkflowBuilder()
                .setBaseWorkflowFile(baseWfFile)
                .setParams(params)
                .setOutputDirectory(outputDirectory) // must always be set
                .build();
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
        if (workflow != null) {
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
            workflow.generateNewSeed();
            
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
            LOG.info("Succesfully connected to comfy.");
        } catch (RuntimeException e) {
            LOG.error("Unable to connect to comfy.");
            e.printStackTrace();
        }
    }

    private void clearQueueHistory() {
        httpClient.post(historyUrl, new JSONObject()
                .put("clear", true));
    }
}
