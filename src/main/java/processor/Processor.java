package main.java.processor;

import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResponse;

public interface Processor {

    public JobResponse doWork(JobRequest request);

}
