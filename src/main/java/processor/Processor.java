package main.java.processor;

import java.util.Map;
import java.util.UUID;

import main.java.processor.models.ProcessorResponse;

public interface Processor {

    public ProcessorResponse doWork(UUID id, Map<String, String> params);

}
