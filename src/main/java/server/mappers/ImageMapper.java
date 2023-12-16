package main.java.server.mappers;

import main.java.common.db.models.ImageDbModel;

import java.time.Instant;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.models.JobState;
import main.java.common.models.JobType;
import main.java.common.mq.ZMQModel;
import main.java.server.models.image.GetImageResponse;
import main.java.server.models.image.PostImageRequest;

public class ImageMapper implements JobMapper<GetImageResponse, PostImageRequest, ImageDbModel> {

    @Inject private Gson gson;

    @Override
    public GetImageResponse mapFromDBModel(ImageDbModel model) {
        return GetImageResponse.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .state(model.getState())
                .notes(model.getNotes())
                .errors(model.getErrors())
                .baseImageId(model.getBaseImageId())
                .steps(model.getSteps())
                .params(model.getParams())
                .outputFilename(model.getOutputFilename())
                .build();
    }

    @Override
    public ImageDbModel mapToDBModel(PostImageRequest model) {
        return ImageDbModel.builder()
                .id(model.getGeneratedId())
                .createdOn(Instant.now())
                .lastModifiedOn(Instant.now())
                .state(JobState.SUBMITTED)
                .baseImageId(model.getBaseImageId())
                .steps(model.getSteps())
                .params(model.getParams())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(PostImageRequest model) {
        return ZMQModel.builder()
                .id(model.getGeneratedId())
                .baseJobId(model.getBaseImageId())
                .jobType(JobType.IMAGE)
                //.steps(gson.toJson(model.getSteps()))
                .params(gson.toJson(model.getParams()))
                .build();
    }

}
