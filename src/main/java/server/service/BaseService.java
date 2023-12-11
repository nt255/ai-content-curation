package main.java.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.BaseDbModel;
import main.java.server.mappers.Mapper;
import main.java.server.models.BaseModel;

public abstract class BaseService<S extends BaseModel, T extends BaseDbModel> {

    @Inject private BaseDao<T> dao;
    @Inject protected Mapper<S, T> mapper;

    public List<S> getCollection() {
        throw new UnsupportedOperationException();
    }

    public Optional<S> get(UUID id) {
        return dao.get(id).map(model -> mapper.mapFromDBModel(model));
    }

    public void insert(S model) {
        dao.insert(mapper.mapToDBModel(model));
    }

    public void delete(UUID id) {
        dao.delete(id);
    }

}
