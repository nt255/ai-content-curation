package processors;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZMQSubscriber;
import common.mq.ZMQModel;
import processors.impl.TextOnlyProcessor;
import processors.models.JobRequest;
import processors.models.JobResponse;


public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private TextOnlyProcessor textOnlyProcessor;
    @Inject private ZMQSubscriber subscriber;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ProcessorModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {
        LOG.info("Starting Processor.");
        subscriber.connectSocket();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOG.info("Waiting for payload...");
            ZMQModel model = subscriber.receive();
        }


        subscriber.closeSocket();
        LOG.info("Closing Processor.");

        JobRequest jobRequest = JobRequest.builder()
                .input("Write me a nice story about a girl on a farm.")
                .build();

        JobResponse jobResponse = textOnlyProcessor.doWork(jobRequest);

        LOG.info(jobResponse.getResult());
    }

}
