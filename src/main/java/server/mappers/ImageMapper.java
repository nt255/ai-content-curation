package main.java.server.mappers;

import main.java.common.db.models.ImageDbModel;

import java.time.Instant;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.models.JobState;
import main.java.common.models.JobType;
import main.java.common.models.image.ImageParams;
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
                .baseImageId(model.getBaseImageId())
                .steps(model.getSteps())
                .params(model.getParams())
                .outputFilename(model.getOutputFilename())
                .build();
    }

    @Override
    public ImageDbModel mapToDBModel(UUID generatedId, PostImageRequest model) {
        return ImageDbModel.builder()
                .id(generatedId)
                .createdOn(Instant.now())
                .lastModifiedOn(Instant.now())
                .state(JobState.SUBMITTED)
                .baseImageId(model.getBaseImageId())
                .steps(model.getParams().stream()
                        .map(ImageParams::getType)
                        .toList())
                .params(model.getParams())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(UUID generatedId, PostImageRequest model) {
        return ZMQModel.builder()
                .id(generatedId)
                .baseJobId(model.getBaseImageId())
                .jobType(JobType.IMAGE)
                .params(gson.toJson(model.getParams()))
                .build();
    }

}
