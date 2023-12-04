package server;


import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.enums.JobType;
import common.mq.ZMQModel;
import common.mq.ZMQPublisher;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private ZMQPublisher publisher;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ServerModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {

        LOG.info("Starting Server.");
        publisher.bindSocket();

        for (int i = 0; i != 10; ++i) {
            publisher.send(ZMQModel.builder()
                    .jobType(JobType.TEXT_ONLY)
                    .id(UUID.randomUUID())
                    .parameters(Map.of("prompt", "Write me a nice story about a distinguished gardener."))
                    .build());
        }

        publisher.closeSocket();
        LOG.info("Closing Server.");
    }

}
