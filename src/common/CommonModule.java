package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.zeromq.ZMQ;
import org.zeromq.SocketType;
import org.zeromq.ZContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import common.clients.HttpClient;
import common.mq.ZMQClient;
import common.mq.ZMQServer;

public class CommonModule extends AbstractModule {

    @Override
    protected void configure() {

        // clients
        bind(HttpClient.class).in(Singleton.class);
        
        // ZMQ
        bind(ZMQClient.class).in(Singleton.class);
        bind(ZMQServer.class).in(Singleton.class);
    }

    
    @Provides
    public Properties provideProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/config.properties"));
        } catch (IOException e) {
            System.err.println("failed to open properties file");
            e.printStackTrace();
        }
        return properties;
    }

}