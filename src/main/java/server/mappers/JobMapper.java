package main.java.server.mappers;

import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQModel;
import main.java.server.models.Job;

public interface JobMapper<S extends Job, T extends JobDbModel> extends Mapper<S, T> {
    
    public ZMQModel mapToZMQModel(S model);

}
