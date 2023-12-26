package main.java.common;

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

import main.java.common.adapters.GsonInstantTypeAdapter;
import main.java.common.clients.HttpClient;
import main.java.common.db.client.MongoDBClient;
import main.java.common.db.dao.BaseDao;
import main.java.common.db.dao.ImageDao;
import main.java.common.db.dao.TextDao;
import main.java.common.db.models.ImageDbModel;
import main.java.common.db.models.TextDbModel;
import main.java.common.file.FileServer;
import main.java.common.file.LocalFileServer;

public class CommonModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(CommonModule.class);

    @Override
    protected void configure() {

        // clients
        bind(HttpClient.class).in(Singleton.class);

        // file
        bind(FileServer.class).to(LocalFileServer.class);

        // db
        bind(MongoDBClient.class).asEagerSingleton();
        bind(new TypeLiteral<BaseDao<TextDbModel>>(){}).to(TextDao.class);
        bind(new TypeLiteral<BaseDao<ImageDbModel>>(){}).to(ImageDao.class);
    }


    @Provides
    public Properties provideProperties() {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = 
                    new FileInputStream("src/config.properties");
            properties.load(fileInputStream);
            fileInputStream.close();
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