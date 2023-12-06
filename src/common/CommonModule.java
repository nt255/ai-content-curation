package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import common.adapters.GsonInstantTypeAdapter;
import common.clients.HttpClient;
import common.db.client.MongoDBClient;
import common.db.dao.BaseDao;
import common.db.dao.JobDao;
import common.db.models.Job;
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

        // dao
        bind(MongoDBClient.class).asEagerSingleton();   // eager singleton needed here to avoid possible deadlock
        bind(new TypeLiteral<BaseDao<Job>>(){}).to(JobDao.class);
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

    @Provides
    public Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new GsonInstantTypeAdapter())
                .create();
    }

}