package main.java.server.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQProducer;
import main.java.server.models.Job;

public class JobService extends BaseService<Job, JobDbModel> {

    private static final Logger LOG = LoggerFactory.getLogger(JobService.class);

    @Inject private ZMQProducer producer;

    public void submit(UUID id) {        
        get(id).ifPresentOrElse(
                job -> {
                    LOG.info("submitting job with id: {} to queue", id);
                    producer.send(mapper.mapToZMQModel(job));
                }, 
                () -> LOG.warn("unable to find job with id: {}", id));
    }

}
