package server.mappers;


import common.db.models.JobDbModel;
import common.mq.ZMQModel;
import server.models.Job;


public class JobMapper implements Mapper<Job, JobDbModel> {

    @Override
    public JobDbModel mapToDBModel(Job model) {
        return JobDbModel.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .type(model.getType())
                .state(model.getState())
                .parameters(model.getParameters())
                .textResult(model.getTextResult())
                .imageResult(model.getImageResult())
                .build();
    }

    @Override
    public Job mapFromDBModel(JobDbModel model) {
        return Job.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .type(model.getType())
                .state(model.getState())
                .parameters(model.getParameters())
                .textResult(model.getTextResult())
                .imageResult(model.getImageResult())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(Job model) {
        return ZMQModel.builder()
                .id(model.getId())
                .jobType(model.getType())
                .parameters(model.getParameters())
                .build();
    }


}
