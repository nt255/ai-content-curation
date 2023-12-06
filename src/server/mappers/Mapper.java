package server.mappers;

import common.mq.ZMQModel;
import server.models.BaseModel;


public interface Mapper<S extends BaseModel, T extends common.db.models.BaseModel> {
    
    public T mapToDBModel(S model);
    
    public S mapFromDBModel(T model);
    
    public ZMQModel mapToZMQModel(S model);

}
