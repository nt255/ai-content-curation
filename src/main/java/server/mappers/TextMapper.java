package main.java.server.mappers;

import java.util.Map;

import main.java.common.db.models.TextDbModel;
import main.java.common.enums.JobType;
import main.java.common.mq.ZMQModel;
import main.java.server.models.Text;

public class TextMapper implements JobMapper<Text, TextDbModel> {

    @Override
    public TextDbModel mapToDBModel(Text model) {
        return TextDbModel.builder()
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
                .outputText(model.getOutputText())
                .build();
    }

    @Override
    public Text mapFromDBModel(TextDbModel model) {
        return Text.builder()
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
                .outputText(model.getOutputText())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(Text model) {
        Map<String, String> params = Map.of("prompt", model.getInputText());
        return ZMQModel.builder()
                .id(model.getId())
                .jobType(JobType.TEXT)
                .parameters(params)
                .build();
    }

}
