package main.java.processor;


import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import main.java.common.mq.ZMQConsumer;
import main.java.processor.impl.TextProcessor;
import main.java.processor.text.ChatGPTClient;
import main.java.processor.text.GPT4AllLoader;


public class ProcessorModule extends AbstractModule {

    @Override
    protected void configure() {
                
        bind(ZMQConsumer.class).asEagerSingleton();

        // clients
        bind(ChatGPTClient.class).in(Singleton.class);
        
        // processors
        bind(ProcessorRouter.class).in(Singleton.class);
        bind(TextProcessor.class).in(Singleton.class);
        
        // loaders
        bind(GPT4AllLoader.class).in(Singleton.class);
    }
    
    
}
