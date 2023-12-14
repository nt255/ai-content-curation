package main.java.processor;

import java.util.UUID;

import main.java.common.models.BaseParams;
import main.java.processor.models.ProcessorResult;

public interface Processor<T extends BaseParams> {

    public ProcessorResult process(UUID id, T params);
    
    public void save(UUID id, ProcessorResult result);

}
