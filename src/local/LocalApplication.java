package local;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import common.CommonModule;

public class LocalApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LocalApplication.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule());
        LocalApplication application = injector.getInstance(LocalApplication.class);
        application.start(args);
    }

    private void start(String[] args) {

        LOG.info("Starting LocalApplication.");

        CompletableFuture<Void> serverFuture =
                CompletableFuture.runAsync(() -> server.Application.main(args));

        CompletableFuture<Void> processorFuture =
                CompletableFuture.runAsync(() -> processors.Application.main(args));

        CompletableFuture.allOf(serverFuture, processorFuture).toCompletableFuture().join();
    }

}
