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
import main.java.server.models.image.GetImageResponse;
import main.java.server.models.image.CreateImageRequest;
import main.java.server.models.text.GetTextResponse;
import main.java.server.models.text.CreateTextRequest;
import main.java.server.request.CombinedRequestHandler;
import main.java.server.validator.ImageValidator;
import main.java.server.validator.TextValidator;

class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(ZMQProducer.class).asEagerSingleton();
        
        // request
        bind(CombinedRequestHandler.class).in(Singleton.class);
        
        // validators
        bind(TextValidator.class).in(Singleton.class);
        bind(ImageValidator.class).in(Singleton.class);
        
        
        // mappers
        bind(new TypeLiteral<
                JobMapper<GetTextResponse, CreateTextRequest, TextDbModel>>(){})
        .to(TextMapper.class);
        
        bind(new TypeLiteral<
                JobMapper<GetImageResponse, CreateImageRequest, ImageDbModel>>(){})
        .to(ImageMapper.class);
        
    }

}
