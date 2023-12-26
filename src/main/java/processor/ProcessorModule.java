package main.java.processor;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import main.java.common.mq.ZMQConsumer;
import main.java.processor.gpt.GPT4AllLoader;
import main.java.processor.image.ImageProcessor;
import main.java.processor.text.TextProcessor;

class ProcessorModule extends AbstractModule {

    @Override
    protected void configure() {
                
        bind(ZMQConsumer.class).asEagerSingleton();
        
        // processors
        bind(ProcessorRouter.class).in(Singleton.class);
        bind(TextProcessor.class).in(Singleton.class);
        bind(ImageProcessor.class).in(Singleton.class);
        
        // loaders
        bind(GPT4AllLoader.class).in(Singleton.class);
    }

}
