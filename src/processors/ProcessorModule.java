package processors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import processors.clients.ChatGPTClient;
import processors.impl.TextOnlyProcessor;


public class ProcessorModule extends AbstractModule {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProcessorModule.class);
    

    @Override
    protected void configure() {

        // clients
        bind(ChatGPTClient.class).in(Singleton.class);

        // processors
        bind(ProcessorRouter.class).in(Singleton.class);
        bind(TextOnlyProcessor.class).in(Singleton.class);
    }
    

}
