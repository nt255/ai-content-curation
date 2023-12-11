package main.java.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import main.java.common.CommonModule;
import main.java.server.request.RequestHandler;


public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private RequestHandler requestHandler;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ServerModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {
        LOG.info("Starting Server.");
        
        requestHandler.start();
    }

}
