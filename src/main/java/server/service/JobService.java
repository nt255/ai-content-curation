package main.java.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.JobMapper;
import main.java.server.models.Job;

public abstract class JobService<S extends Job, T extends JobDbModel> 
extends BaseService<S, T> {

    private static final Logger LOG = LoggerFactory.getLogger(JobService.class);
    
    private JobMapper<S, T> mapper;
    private ZMQProducer producer;
    
    @Inject
    public JobService(BaseDao<T> dao, JobMapper<S, T> mapper, 
            ZMQProducer producer) {
        super(dao, mapper);
        this.mapper = mapper;
        this.producer = producer;
    }
    
    public void create(S model) {
        super.create(model);
        
        LOG.info("submitting job with id: {} to queue", model.getId());
        producer.send(mapper.mapToZMQModel(model));
    }

}
