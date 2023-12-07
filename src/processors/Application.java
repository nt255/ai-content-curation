package processors;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZMQSubscriber;
import common.mq.ZMQModel;

import processors.models.JobResponse;
import processors.clients.ComfyClient;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private ZMQSubscriber subscriber;
    @Inject private ProcessorRouter router;
    private ComfyClient comfyClient;

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
        	comfyClient.queuePrompt();
        } catch (IllegalStateException e) {
        	LOG.error("Could not queue prompt!");
        	LOG.error(e.getMessage());
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LOG.info("Waiting for payload...");
            ZMQModel model = subscriber.receive();

            JobResponse response = router.route(
                    model.getJobType(), model.getId(), model.getParameters());
            UUID id = response.getId();

            if (response.isSuccessful())
                LOG.info("Succesfully processed job with id: {}.", id);
            else
                LOG.warn("Unable to process job with id: {}.", id);
        }
    }
}
