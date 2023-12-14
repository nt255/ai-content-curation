package main.java.server.mappers;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.db.models.TextDbModel;
import main.java.common.models.JobType;
import main.java.common.mq.ZMQModel;
import main.java.server.models.Text;

public class TextMapper implements JobMapper<Text, TextDbModel> {
    
    @Inject private Gson gson;

    @Override
    public TextDbModel mapToDBModel(Text model) {
        return TextDbModel.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .state(model.getState())
                .params(model.getParams())
                .notes(model.getNotes())
                .errors(model.getErrors())
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
                .params(model.getParams())
                .notes(model.getNotes())
                .errors(model.getErrors())
                .outputText(model.getOutputText())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(Text model) {
        return ZMQModel.builder()
                .id(model.getId())
                .jobType(JobType.TEXT)
                .params(gson.toJson(model.getParams()))
                .build();
    }

}
