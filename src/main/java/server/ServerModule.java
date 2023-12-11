package main.java.server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.JobMapper;
import main.java.server.mappers.Mapper;
import main.java.server.models.Job;
import main.java.server.request.RequestHandler;

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
