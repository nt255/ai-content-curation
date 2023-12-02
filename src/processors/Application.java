package processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZMQClient;
import common.mq.models.ZMQModel;
import processors.impl.TextOnlyProcessor;
import processors.models.JobRequest;
import processors.models.JobResponse;


public class Application {
    
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private TextOnlyProcessor textOnlyProcessor;
    @Inject private ZMQClient ZMQClient;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ProcessorModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {
        LOG.info("Starting Processor");
        ZMQClient.connectSocket();

        while (!Thread.currentThread().isInterrupted()) {
            LOG.info("Waiting for payload...");
            ZMQModel model = ZMQClient.receive();
        }
        
        
        ZMQClient.closeSocket();
        LOG.info("Closing Processor.");

        JobRequest jobRequest = JobRequest.builder()
                .input("Write me a nice story about a girl on a farm.")
                .build();

        JobResponse jobResponse = textOnlyProcessor.doWork(jobRequest);

        LOG.info(jobResponse.getResult());
    }

}
