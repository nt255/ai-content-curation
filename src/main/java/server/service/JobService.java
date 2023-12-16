package main.java.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.JobMapper;
import main.java.server.models.BasePostRequest;
import main.java.server.models.GetJobResponse;

public abstract class JobService<S extends GetJobResponse, T extends BasePostRequest, U extends JobDbModel> 
extends BaseService<S, T, U> {

    private static final Logger LOG = LoggerFactory.getLogger(JobService.class);
    
    private JobMapper<S, T, U> mapper;
    private ZMQProducer producer;
    
    @Inject
    public JobService(BaseDao<U> dao, JobMapper<S, T, U> mapper, 
            ZMQProducer producer) {
        super(dao, mapper);
        this.mapper = mapper;
        this.producer = producer;
    }
    
    public void create(T model) {
        super.create(model);
        
        LOG.info("submitting job with id: {} to queue", model.getGeneratedId());
        producer.send(mapper.mapToZMQModel(model));
    }

}
