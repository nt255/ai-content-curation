package server.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import common.mq.ZMQPublisher;
import server.models.Job;

public class JobService extends BaseService<Job, common.db.models.JobDbModel> {

    private static final Logger LOG = LoggerFactory.getLogger(JobService.class);

    @Inject private ZMQPublisher publisher;

    public void submit(UUID id) {        
        get(id).ifPresentOrElse(
                job -> {
                    LOG.info("submitting job with id: {} to queue", id);
                    publisher.send(mapper.mapToZMQModel(job));
                }, 
                () -> LOG.warn("unable to find job with id: {}", id));
    }

}
