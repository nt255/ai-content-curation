package main.java.common.db.client;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Properties;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.google.inject.Inject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;


public class MongoDBClient {

    @Getter private final MongoDatabase database;

    @Inject
    public MongoDBClient(final Properties properties) {
        
        String uri = properties.getProperty("mongodb.uri");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
        
        MongoClient client = MongoClients.create(settings);

        // check connection
        Document pingCommand = new Document("ping", 1);
        client.getDatabase("admin").runCommand(pingCommand);
        
        PojoCodecProvider pojoCodecProvider = 
                PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = 
                fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        String databaseName = properties.getProperty("mongodb.database.name");
        database = client.getDatabase(databaseName)
                .withCodecRegistry(codecRegistry);
    }

}
