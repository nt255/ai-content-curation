package main.java.processor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import main.java.common.CommonModule;
import main.java.common.mq.ZMQConsumer;
import main.java.common.mq.ZMQModel;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Inject private ZMQConsumer consumer;
    @Inject private ProcessorRouter router;

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new CommonModule(),
                new ProcessorModule());
        Application application = injector.getInstance(Application.class);
        application.start(args);
    }

    private void start(String[] args) {

        LOG.info("Starting Processor.");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                LOG.info("Waiting for payload...");
                ZMQModel model = consumer.receive();

                router.processAndSave(
                        model.getJobType(), model.getId(), model.getParams());
            }
        });

        try {
            if (args.length == 1) {
                Long timeout = Long.parseLong(args[0]);
                future.get(timeout, TimeUnit.MILLISECONDS);
            } else {
                future.get();
            }
        } catch (TimeoutException e) {
            LOG.info(e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        LOG.info("Exiting Processor.");
    }
}
