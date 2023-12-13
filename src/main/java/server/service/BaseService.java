package main.java.server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.inject.Inject;

import main.java.common.db.dao.BaseDao;
import main.java.common.db.models.JobDbModel;
import main.java.server.mappers.Mapper;
import main.java.server.models.BaseModel;

public abstract class BaseService<S extends BaseModel, T extends JobDbModel> {

    private BaseDao<T> dao;
    Mapper<S, T> mapper;
    
    @Inject
    public BaseService(BaseDao<T> dao, Mapper<S, T> mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

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
