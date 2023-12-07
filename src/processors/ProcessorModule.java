package processors;


import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import processors.clients.ChatGPTClient;
import processors.clients.WorkflowLoader;
import processors.impl.TextOnlyProcessor;


public class ProcessorModule extends AbstractModule {

    @Override
    protected void configure() {

        // clients
        bind(ChatGPTClient.class).in(Singleton.class);
        
        // WorkflowLoader instances
        bind(WorkflowLoader.class).annotatedWith(Names.named("fitnessAesthetics")).toInstance(new WorkflowLoader("fitnessAesthetics"));
        bind(WorkflowLoader.class).annotatedWith(Names.named("dailyAffirmations")).toInstance(new WorkflowLoader("dailyAffirmations"));
        bind(WorkflowLoader.class).annotatedWith(Names.named("thirstTrap")).toInstance(new WorkflowLoader("thirstTrap"));

        // processors
        bind(ProcessorRouter.class).in(Singleton.class);
        bind(TextOnlyProcessor.class).in(Singleton.class);
    }
    
    
}
