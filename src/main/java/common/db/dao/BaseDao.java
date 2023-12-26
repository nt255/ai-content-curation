package main.java.common.db.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;

import main.java.common.db.client.MongoDBClient;
import main.java.common.db.models.BaseDbModel;

public abstract class BaseDao<T extends BaseDbModel> {

    private static final String ID = "_id";

    @Inject private MongoDBClient mongoDBClient;

    private final Class<T> typeParameterClass;
    private final String collectionName;

    BaseDao(Class<T> typeParameterClass, String collectionName) {
        this.typeParameterClass = typeParameterClass;
        this.collectionName = collectionName;
    }

    private MongoCollection<T> getCollection() {
        return mongoDBClient.getDatabase()
                .getCollection(collectionName, typeParameterClass);
    }

    public final Optional<T> get(UUID id) {
        return Optional.ofNullable(getCollection().find(eq(ID, id))
                .first());
    }

    public final void insert(T document) {
        getCollection().insertOne(document);
    }

    public final void delete(UUID id) {
        getCollection().deleteOne(eq(ID, id));
    }

}
