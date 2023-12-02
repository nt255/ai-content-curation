package server;


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZMQServer;
import common.mq.models.ZMQModel;
import common.mq.models.ZMQModel.JobType;

public class Application {
    
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private ZMQServer ZMQServer;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ServerModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {

        LOG.info("Starting Server.");
        ZMQServer.bindSocket();

        ZMQServer.send(ZMQModel.builder()
                .jobType(JobType.TEXT_ONLY)
                .id(UUID.randomUUID())
                .build());
        
        ZMQServer.send(ZMQModel.builder()
                .jobType(JobType.TEXT_ONLY)
                .id(UUID.randomUUID())
                .build());
        
        
        ZMQServer.closeSocket();
        LOG.info("Closing Server.");
    }

}
