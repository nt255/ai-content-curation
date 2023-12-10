package processors;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZMQSubscriber;
import common.mq.ZMQModel;
import processors.models.ComfyConfigs;
import processors.models.JobResponse;
import processors.clients.ComfyClient;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private ZMQSubscriber subscriber;
    @Inject private ProcessorRouter router;
    @Inject private ComfyClient comfyClient;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ProcessorModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {

        LOG.info("Starting Processor.");
        
        try {
        	Map<String, String> map = new HashMap<String, String>();
        	map.put("height", "768");
        	map.put("checkpoint", "realDream_9.safetensors");
        	map.put("prompt", "realistic photo, film grain, (upper body photo of a confident, muscular asian male model ((looking at viewer))). handsome. wearing gym shorts, topless. curly hair.");
        	ComfyConfigs configs = new ComfyConfigs(map);
        	comfyClient.applyConfigs(configs);
        	comfyClient.queuePrompt(2);
        	comfyClient.switchWorkflow("dailyAffirmations");
        	map.put("prompt", "cool lady riding the subway, realistic.");
        	comfyClient.applyConfigs(configs);
        	comfyClient.queuePrompt(2);
        } catch (IllegalStateException e) {
        	LOG.error("Could not queue prompt!");
        	LOG.error(e.getMessage());
        } catch(Exception e) {
        	LOG.error(e.getMessage());
        }
        
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(() -> {
            LOG.info("Waiting for payload...");
            ZMQModel model = subscriber.receive();

            JobResponse response = router.route(
                    model.getJobType(), model.getId(), model.getParameters());
            UUID id = response.getId();

            if (response.isSuccessful())
                LOG.info("Succesfully processed job with id: {}.", id);
            else
                LOG.warn("Unable to process job with id: {}.", id);
        });

        try {
            if (args.length == 1) {
                Long timeout = Long.parseLong(args[0]);
                future.get(timeout, TimeUnit.MILLISECONDS);
            } else {
                future.get();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        LOG.info("Exiting Processor.");
    }
}
