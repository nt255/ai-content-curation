package processors;

import processors.models.JobRequest;
import processors.models.JobResponse;

public interface Processor {

    public JobResponse doWork(JobRequest request);

}
