package server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import server.mappers.JobMapper;
import server.mappers.Mapper;
import server.models.Job;
import server.request.RequestHandler;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(RequestHandler.class).in(Singleton.class);
        
        // mappers
        bind(new TypeLiteral<Mapper<Job, common.db.models.Job>>(){}).to(JobMapper.class);
        
    }

}
