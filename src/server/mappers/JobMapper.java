package server.mappers;


import java.util.Map;
import java.util.Objects;

import common.mq.ZMQModel;
import server.models.Job;


public class JobMapper implements Mapper<Job, common.db.models.Job> {

    @Override
    public common.db.models.Job mapToDBModel(Job model) {
        return common.db.models.Job.builder()
                .jobType(model.getJobType())
                .jobState(model.getJobState())
                .id(model.getId())
                .prompt(model.getParameters().getOrDefault("prompt", ""))
                .build();
    }

    @Override
    public Job mapFromDBModel(common.db.models.Job model) {
        Map<String, String> params = 
                Map.of("prompt", Objects.toString(model.getPrompt(), ""));
        return Job.builder()
                .jobType(model.getJobType())
                .jobState(model.getJobState())
                .id(model.getId())
                .parameters(params)
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(Job model) {
        return ZMQModel.builder()
                .jobType(model.getJobType())
                .id(model.getId())
                .parameters(model.getParameters())
                .build();
    }


}
