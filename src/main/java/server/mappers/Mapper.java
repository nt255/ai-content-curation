package main.java.server.mappers;

import java.util.UUID;

import main.java.common.db.models.BaseDbModel;
import main.java.server.models.BaseGetResponse;
import main.java.server.models.BaseCreateRequest;

public interface Mapper<
S extends BaseGetResponse, 
T extends BaseCreateRequest, 
U extends BaseDbModel> {
        
    public S mapFromDBModel(U model);
    
    public U mapToDBModel(UUID generatedId, T model);

}
