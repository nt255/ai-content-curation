package main.java.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LocalApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LocalApplication.class);

    public static void main(String[] args) {
        
        LOG.info("Starting LocalApplication.");
        
        main.java.server.Application.main(args);
        main.java.processor.Application.main(args);
    }
}
