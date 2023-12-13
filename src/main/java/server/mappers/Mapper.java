package main.java.server.mappers;

import main.java.common.db.models.BaseDbModel;
import main.java.server.models.BaseModel;


public interface Mapper<S extends BaseModel, T extends BaseDbModel> {
    
    public T mapToDBModel(S model);
    
    public S mapFromDBModel(T model);

}
