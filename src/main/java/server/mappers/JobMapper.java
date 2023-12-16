package main.java.server.mappers;

import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQModel;
import main.java.server.models.BasePostRequest;
import main.java.server.models.GetJobResponse;

public interface JobMapper<S extends GetJobResponse, T extends BasePostRequest, U extends JobDbModel> 
extends Mapper<S, T, U> {
    
    public ZMQModel mapToZMQModel(T model);

}
