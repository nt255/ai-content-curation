package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import common.clients.HttpClient;
import common.db.client.MongoDBClient;
import common.db.dao.JobDAO;
import common.mq.ZMQSubscriber;
import common.mq.ZMQPublisher;

public class CommonModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(CommonModule.class);

    @Override
    protected void configure() {

        // clients
        bind(HttpClient.class).in(Singleton.class);

        // ZMQ
        bind(ZMQSubscriber.class).in(Singleton.class);
        bind(ZMQPublisher.class).in(Singleton.class);

        // db
        bind(MongoDBClient.class).asEagerSingleton();
        bind(JobDAO.class).asEagerSingleton();
    }


    @Provides
    public Properties provideProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/config.properties"));
        } catch (IOException e) {
            LOG.error("failed to open properties file");
            e.printStackTrace();
        }
        return properties;
    }

}