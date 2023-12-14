package main.java.server.mappers;

import main.java.common.db.models.ImageDbModel;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.models.JobType;
import main.java.common.mq.ZMQModel;
import main.java.server.models.Image;

public class ImageMapper implements JobMapper<Image, ImageDbModel> {
    
    @Inject private Gson gson;

    @Override
    public ImageDbModel mapToDBModel(Image model) {
        return ImageDbModel.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .state(model.getState())
                .notes(model.getNotes())
                .errors(model.getErrors())
                .params(model.getParams())
                .outputFilename(model.getOutputFilename())
                .build();
    }

    @Override
    public Image mapFromDBModel(ImageDbModel model) {
        return Image.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .state(model.getState())
                .notes(model.getNotes())
                .errors(model.getErrors())
                .params(model.getParams())
                .outputFilename(model.getOutputFilename())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(Image model) {
        return ZMQModel.builder()
                .id(model.getId())
                .jobType(JobType.IMAGE)
                .params(gson.toJson(model.getParams()))
                .build();
    }

}
