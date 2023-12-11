package main.java.processor.comfy;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;


public class ComfyModule extends AbstractModule {

    @Override
    protected void configure() {
        
        // WorkflowLoader instances
        bind(WorkflowLoader.class).annotatedWith(Names.named("fitnessAesthetics")).toInstance(new WorkflowLoader("fitnessAesthetics"));
        bind(WorkflowLoader.class).annotatedWith(Names.named("dailyAffirmations")).toInstance(new WorkflowLoader("dailyAffirmations"));
        bind(WorkflowLoader.class).annotatedWith(Names.named("upscaler")).toInstance(new WorkflowLoader("upscaler"));
    }
    
    
}
