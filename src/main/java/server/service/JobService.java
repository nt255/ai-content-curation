package main.java.server.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.models.JobDbModel;
import main.java.common.file.FileServer;
import main.java.common.mq.ZMQProducer;
import main.java.server.models.Job;
import zmq.util.function.Optional;

public class JobService extends BaseService<Job, JobDbModel> {

    private static final Logger LOG = LoggerFactory.getLogger(JobService.class);

    @Inject private ZMQProducer producer;
    @Inject private FileServer fileServer;

    public void submit(UUID id) {        
        get(id).ifPresentOrElse(
                job -> {
                    LOG.info("submitting job with id: {} to queue", id);
                    producer.send(mapper.mapToZMQModel(job));
                }, 
                () -> LOG.warn("unable to find job with id: {}", id));
    }

    @Override
    public void delete(UUID id) {
        get(id).ifPresentOrElse(
                job -> {
                    LOG.info("deleting job with id: {}", id);
                    Optional.ofNullable(
                            job.getOutputImageFilename()).ifPresent(fn -> {
                                LOG.info("deleting file: {}", fn);
                                fileServer.deleteFile(fn);
                            });
                    super.delete(id);
                }, 
                () -> LOG.warn("unable to find job with id: {}", id));
    }

}
