package main.java.server.mappers;

import java.util.UUID;

import main.java.common.db.models.BaseDbModel;
import main.java.server.models.BaseGetResponse;
import main.java.server.models.BasePostRequest;

public interface Mapper<
S extends BaseGetResponse, 
T extends BasePostRequest, 
U extends BaseDbModel> {
        
    public S mapFromDBModel(U model);
    
    public U mapToDBModel(UUID generatedId, T model);

}
