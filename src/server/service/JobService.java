package server.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import common.mq.ZMQPublisher;
import server.models.Job;

public class JobService extends BaseService<Job, common.db.models.Job> {
    
    private static final Logger LOG = LoggerFactory.getLogger(JobService.class);
    
    @Inject private ZMQPublisher publisher;
    
    public void submitJob(UUID id) {
        Optional<Job> jobOptional = get(id);
        if (jobOptional.isEmpty()) {
            LOG.info("unable to find job with id: {}, not submitting to queue", id);
        } else {
            LOG.info("submitting job with id: {} to queue", id);
            publisher.send(mapper.mapToZMQModel(jobOptional.get()));
        }
    }

}
