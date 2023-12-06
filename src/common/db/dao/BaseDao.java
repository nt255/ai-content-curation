package common.db.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;

import common.db.client.MongoDBClient;
import common.db.models.BaseModel;

public abstract class BaseDao<T extends BaseModel> {

    private static final String ID = "_id";

    @Inject private MongoDBClient mongoDBClient;

    private final Class<T> typeParameterClass;
    private final String collectionName;

    public BaseDao(Class<T> typeParameterClass, String collectionName) {
        this.typeParameterClass = typeParameterClass;
        this.collectionName = collectionName;
    }

    public MongoCollection<T> getCollection() {
        return mongoDBClient.getDatabase()
                .getCollection(collectionName, typeParameterClass);
    }

    public Optional<T> get(UUID id) {
        return Optional.ofNullable(mongoDBClient.getDatabase()
                .getCollection(collectionName, typeParameterClass)
                .find(eq(ID, id)).first());
    }

    public void insert(T document) {
        mongoDBClient.getDatabase()
        .getCollection(collectionName, typeParameterClass)
        .insertOne(document);
    }

    public void delete(UUID id) {
        mongoDBClient.getDatabase()
        .getCollection(collectionName, typeParameterClass)
        .deleteOne(eq(ID, id));
    }

}
