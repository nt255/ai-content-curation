package processors;


import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import common.mq.ZMQConsumer;
import processors.clients.ChatGPTClient;
import processors.clients.WorkflowLoader;
import processors.clients.GPT4AllLoader;
import processors.impl.TextOnlyProcessor;


public class ProcessorModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(ZMQConsumer.class).asEagerSingleton();

        // clients
        bind(ChatGPTClient.class).in(Singleton.class);
        
        // WorkflowLoader instances
        bind(WorkflowLoader.class).annotatedWith(Names.named("fitnessAesthetics")).toInstance(new WorkflowLoader("fitnessAesthetics"));
        bind(WorkflowLoader.class).annotatedWith(Names.named("dailyAffirmations")).toInstance(new WorkflowLoader("dailyAffirmations"));
        bind(WorkflowLoader.class).annotatedWith(Names.named("upscaler")).toInstance(new WorkflowLoader("upscaler"));

        // processors
        bind(ProcessorRouter.class).in(Singleton.class);
        bind(TextOnlyProcessor.class).in(Singleton.class);
        
        // loaders
        bind(GPT4AllLoader.class).in(Singleton.class);
    }
    
    
}
