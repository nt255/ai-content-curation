package main.java.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.BaseDbModel;
import main.java.server.mappers.Mapper;
import main.java.server.models.BaseGetResponse;
import main.java.server.models.BasePostRequest;

public abstract class BaseService<
S extends BaseGetResponse, 
T extends BasePostRequest, 
U extends BaseDbModel> {

    private BaseDao<U> dao;
    private Mapper<S, T, U> mapper;
    
    @Inject
    public BaseService(BaseDao<U> dao, Mapper<S, T, U> mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    public List<S> getCollection() {
        throw new UnsupportedOperationException();
    }

    public Optional<S> get(UUID id) {
        return dao.get(id).map(model -> mapper.mapFromDBModel(model));
    }

    public UUID create(T model) {
        UUID generatedId = UUID.randomUUID();
        dao.insert(mapper.mapToDBModel(generatedId, model));
        return generatedId;
    }

    public void delete(UUID id) {
        dao.delete(id);
    }

}
