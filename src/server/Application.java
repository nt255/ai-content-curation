package server;


import java.util.UUID;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZMQServer;
import common.mq.models.MessageQueueModel;
import common.mq.models.MessageQueueModel.JobType;

public class Application {

    @Inject private ZMQServer ZMQServer;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ServerModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {

        System.out.println("Starting Server.");
        ZMQServer.bindSocket();

        ZMQServer.send(MessageQueueModel.builder()
                .jobType(JobType.TEXT_ONLY)
                .id(UUID.randomUUID())
                .build());
        
        ZMQServer.send(MessageQueueModel.builder()
                .jobType(JobType.TEXT_ONLY)
                .id(UUID.randomUUID())
                .build());
        
        
        ZMQServer.closeSocket();
        System.out.println("Closing Server.");
        
        // TODO: receiver code
        
//        Thread thread = new Thread(() -> {
//            while (true) {
//                MessageQueueModel model = ZMQServer.receive();
//                System.out.println("received model: " + model.toString());
//            }
//        });
//        thread.start();
    }

}
