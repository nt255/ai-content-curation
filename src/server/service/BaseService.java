package server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;

import common.db.dao.BaseDao;
import common.db.models.BaseDbModel;
import server.mappers.Mapper;
import server.models.BaseModel;

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
