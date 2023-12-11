package main.java.local;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LocalApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LocalApplication.class);

    public static void main(String[] args) {

        LOG.info("Starting LocalApplication.");

        CompletableFuture<Void> serverFuture =
                CompletableFuture.runAsync(() -> main.java.server.Application.main(args));

        CompletableFuture<Void> processorFuture =
                CompletableFuture.runAsync(() -> main.java.processor.Application.main(args));

        CompletableFuture.allOf(serverFuture, processorFuture).toCompletableFuture().join();
    }
}
