package processors;


import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import processors.clients.ChatGPTClient;
import processors.clients.GPT4AllLoader;
import processors.impl.TextOnlyProcessor;


public class ProcessorModule extends AbstractModule {

    @Override
    protected void configure() {

        // clients
        bind(ChatGPTClient.class).in(Singleton.class);

        // processors
        bind(ProcessorRouter.class).in(Singleton.class);
        bind(TextOnlyProcessor.class).in(Singleton.class);
        
        // loaders
        bind(GPT4AllLoader.class).in(Singleton.class);
    }
    

}
