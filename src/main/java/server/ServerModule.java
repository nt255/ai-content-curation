package main.java.server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import main.java.common.db.models.ImageDbModel;
import main.java.common.db.models.TextDbModel;
import main.java.common.mq.ZMQProducer;
import main.java.server.mappers.ImageMapper;
import main.java.server.mappers.JobMapper;
import main.java.server.mappers.TextMapper;
import main.java.server.models.Image;
import main.java.server.models.Text;
import main.java.server.request.RequestHandler;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(ZMQProducer.class).asEagerSingleton();
        
        // request
        bind(RequestHandler.class).in(Singleton.class);
        
        // mappers
        bind(new TypeLiteral<JobMapper<Text, TextDbModel>>(){}).to(TextMapper.class);
        bind(new TypeLiteral<JobMapper<Image, ImageDbModel>>(){}).to(ImageMapper.class);
        
    }

}
