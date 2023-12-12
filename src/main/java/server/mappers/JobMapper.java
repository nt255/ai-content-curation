package main.java.server.mappers;


import main.java.common.db.models.JobDbModel;
import main.java.common.mq.ZMQModel;
import main.java.server.models.Job;


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
                .outputText(model.getOutputText())
                .outputImageFilename(model.getOutputImageFilename())
                .errors(model.getErrors())
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
                .outputText(model.getOutputText())
                .outputImageFilename(model.getOutputImageFilename())
                .errors(model.getErrors())
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
