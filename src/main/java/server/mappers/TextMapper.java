package main.java.server.mappers;

import java.time.Instant;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.inject.Inject;

import main.java.common.db.models.TextDbModel;
import main.java.common.models.JobState;
import main.java.common.models.JobType;
import main.java.common.models.text.TextParams;
import main.java.common.mq.ZMQModel;
import main.java.server.models.text.GetTextResponse;
import main.java.server.models.text.CreateTextRequest;

public class TextMapper implements JobMapper<GetTextResponse, CreateTextRequest, TextDbModel> {

    @Inject private Gson gson;

    @Override
    public GetTextResponse mapFromDBModel(TextDbModel model) {
        return GetTextResponse.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .state(model.getState())
                .steps(model.getSteps())
                .params(model.getParams())
                .outputText(model.getOutputText())
                .build();
    }

    @Override
    public TextDbModel mapToDBModel(UUID generatedId, CreateTextRequest model) {
        return TextDbModel.builder()
                .id(generatedId)
                .createdOn(Instant.now())
                .lastModifiedOn(Instant.now())
                .state(JobState.SUBMITTED)
                .steps(model.getParams().stream()
                        .map(TextParams::getType)
                        .toList())
                .params(model.getParams())
                .build();
    }

    @Override
    public ZMQModel mapToZMQModel(UUID generatedId, CreateTextRequest model) {
        return ZMQModel.builder()
                .id(generatedId)
                .jobType(JobType.TEXT)
                .params(gson.toJson(model.getParams()))
                .build();
    }

}
