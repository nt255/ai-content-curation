package main.java.processor;
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

import main.java.common.CommonModule;
import main.java.common.mq.ZMQConsumer;
import main.java.common.mq.ZMQModel;
import main.java.processor.comfy.ComfyClient;
import main.java.processor.comfy.ComfyConfigs;
import main.java.processor.models.JobResponse;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private ZMQConsumer consumer;
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
        //                try {
        //                	Map<String, String> map = new HashMap<String, String>();
        //                	map.put("height", "768");
        //                	//map.put("checkpoint", "realDream_9.safetensors");
        //                	map.put("checkpoint", "realDream_8Legendary.safetensors");
        //                	map.put("prompt", "realistic photo, film grain, (upper body photo of a confident, muscular asian male model ((looking at viewer))). handsome. wearing gym shorts, topless. curly hair.");
        //                	ComfyConfigs configs = new ComfyConfigs(map);
        //                	comfyClient.applyConfigs(configs);
        //                	comfyClient.queuePrompt(2);
        //                	comfyClient.switchWorkflow("dailyAffirmations");
        //                	map.put("prompt", "cool lady riding the subway, realistic.");
        //                	comfyClient.applyConfigs(configs);
        //                	comfyClient.queuePrompt(2);
        //                } catch (IllegalStateException e) {
        //                	LOG.error("Could not queue prompt!");
        //                	LOG.error(e.getMessage());
        //                } catch(Exception e) {
        //                	LOG.error(e.getMessage());
        //                }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                LOG.info("Waiting for payload...");
                ZMQModel model = consumer.receive();

                JobResponse response = router.route(
                        model.getJobType(), model.getId(), model.getParameters());
                UUID id = response.getId();

                if (response.isSuccessful())
                    LOG.info("Succesfully processed job with id: {}.", id);
                else
                    LOG.warn("Unable to process job with id: {}.", id);
            }
        });

        try {
            if (args.length == 1) {
                Long timeout = Long.parseLong(args[0]);
                future.get(timeout, TimeUnit.MILLISECONDS);
            } else {
                future.get();
            }
        } catch (TimeoutException e) {
            LOG.info("timeout has been reached");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        LOG.info("Exiting Processor.");
    }
}
