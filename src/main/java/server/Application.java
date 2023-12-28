package main.java.server;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import lombok.Getter;
import main.java.common.CommonModule;
import main.java.common.mq.ZMQProducer;

public class Application {
    
    @Getter private static Application application;

    @Inject private JavalinServer server;
    @Inject private ZMQProducer producer;
    
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ServerModule());
        application = injector.getInstance(Application.class);
    }
    
    public void close() {
        server.close();
        producer.close();
    }

}
