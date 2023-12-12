package main.java.processor;

import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResult;

public interface Processor {

    public JobResult doWork(JobRequest request);

}
