package main.java.processor;

import java.util.List;
import java.util.UUID;

import main.java.common.models.BaseParams;
import main.java.processor.models.ProcessorResult;

public interface MultistepProcessor<T extends BaseParams> {

    public ProcessorResult process(UUID id, List<T> steps);
    
    public void save(UUID id, ProcessorResult result);

}
