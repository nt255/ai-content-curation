package server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import common.db.models.JobDbModel;
import common.mq.ZMQProducer;
import server.mappers.JobMapper;
import server.mappers.Mapper;
import server.models.Job;
import server.request.RequestHandler;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(ZMQProducer.class).asEagerSingleton();
        
        // request
        bind(RequestHandler.class).in(Singleton.class);
        
        // mappers
        bind(new TypeLiteral<Mapper<Job, JobDbModel>>(){}).to(JobMapper.class);
        
    }

}
