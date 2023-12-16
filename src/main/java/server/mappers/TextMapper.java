package main.java.server.mappers;

import java.time.Instant;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.db.models.TextDbModel;
import main.java.common.models.JobState;
import main.java.common.models.JobType;
import main.java.common.mq.ZMQModel;
import main.java.server.models.text.GetTextResponse;
import main.java.server.models.text.PostTextRequest;

public class TextMapper implements JobMapper<GetTextResponse, PostTextRequest, TextDbModel> {

    @Inject private Gson gson;

    @Override
    public GetTextResponse mapFromDBModel(TextDbModel model) {
        return GetTextResponse.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .state(model.getState())
                .notes(model.getNotes())
                .errors(model.getErrors())
                .steps(model.getSteps())
                .params(model.getParams())
                .outputText(model.getOutputText())
                .build();
    }

    @Override
    public TextDbModel mapToDBModel(PostTextRequest model) {
        return TextDbModel.builder()
                .id(model.getGeneratedId())
                .createdOn(Instant.now())
                .lastModifiedOn(Instant.now())
                .state(JobState.SUBMITTED)
                .steps(model.getSteps())
                .params(model.getParams())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(PostTextRequest model) {
        return ZMQModel.builder()
                .id(model.getGeneratedId())
                .jobType(JobType.TEXT)
                //.steps(gson.toJson(model.getSteps()))
                .params(gson.toJson(model.getParams()))
                .build();
    }

}
