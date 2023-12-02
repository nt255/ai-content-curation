package server;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import common.CommonModule;
import common.mq.ZeroMQImpl;

public class Application {

    @Inject private ZeroMQImpl messageQueue;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ServerModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    public void start(String[] args) {

        System.out.println("test");
    }
    
}
