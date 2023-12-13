package main.java.server.mappers;

import java.util.Map;

import main.java.common.db.models.ImageDbModel;
import main.java.common.enums.JobType;
import main.java.common.mq.ZMQModel;
import main.java.server.models.Image;

public class ImageMapper implements JobMapper<Image, ImageDbModel> {

    @Override
    public ImageDbModel mapToDBModel(Image model) {
        return ImageDbModel.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .state(model.getState())
                .inputType(model.getInputType())
                .inputText(model.getInputText())
                .inputFilename(model.getInputFilename())
                .notes(model.getNotes())
                .errors(model.getErrors())
                .type(model.getType())
                .height(model.getHeight())
                .width(model.getWidth())
                .checkpoint(model.getCheckpoint())
                .workflow(model.getWorkflow())
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
                .inputType(model.getInputType())
                .inputText(model.getInputText())
                .inputFilename(model.getInputFilename())
                .notes(model.getNotes())
                .errors(model.getErrors())
                .type(model.getType())
                .height(model.getHeight())
                .width(model.getWidth())
                .checkpoint(model.getCheckpoint())
                .workflow(model.getWorkflow())
                .outputFilename(model.getOutputFilename())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(Image model) {
        Map<String, String> params = Map.of(
                "prompt", model.getInputText(),
                "height", model.getHeight().toString(),
                "width", model.getWidth().toString(),
                "checkpoint", model.getCheckpoint(),
                "workflow", model.getWorkflow());
        return ZMQModel.builder()
                .id(model.getId())
                .jobType(JobType.IMAGE)
                .parameters(params)
                .build();
    }

}
