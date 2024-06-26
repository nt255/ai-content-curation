package main.java.server.mappers;

import java.util.UUID;

import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQModel;
import main.java.server.models.BaseCreateRequest;
import main.java.server.models.GetJobResponse;

public interface JobMapper<
S extends GetJobResponse, 
T extends BaseCreateRequest, 
U extends JobDbModel> extends Mapper<S, T, U> {
    
    public ZMQModel mapToZMQModel(UUID generatedId, T model);

}
