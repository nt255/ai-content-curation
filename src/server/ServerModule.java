package server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import server.request.RequestHandler;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        
        bind(RequestHandler.class).in(Singleton.class);
        
    }

}
