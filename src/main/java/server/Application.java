package main.java.server;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import main.java.common.CommonModule;

public class Application {

    @Inject private JavalinServer javalinServer;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ServerModule());
        Application application = injector.getInstance(Application.class);
        application.start();
    }

    private void start() {
        javalinServer.start();
    }

}
