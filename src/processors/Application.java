package processors;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZMQClient;
import common.mq.models.MessageQueueModel;
import processors.impl.TextOnlyProcessor;
import processors.models.JobRequest;
import processors.models.JobResponse;


public class Application {

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
        
        System.out.println("Starting Processor.");
        ZMQClient.connectSocket();

        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Waiting for payload...");
            MessageQueueModel model = ZMQClient.receive();
        }
        
        
        ZMQClient.closeSocket();
        System.out.println("Closing Processor.");

        JobRequest jobRequest = JobRequest.builder()
                .input("Write me a nice story about a girl on a farm.")
                .build();

        JobResponse jobResponse = textOnlyProcessor.doWork(jobRequest);

        System.out.println(jobResponse.getResult());
    }

}
