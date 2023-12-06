package server.mappers;


import common.mq.ZMQModel;
import server.models.Job;


public class JobMapper implements Mapper<Job, common.db.models.Job> {

    @Override
    public common.db.models.Job mapToDBModel(Job model) {
        return common.db.models.Job.builder()
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
    public Job mapFromDBModel(common.db.models.Job model) {
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