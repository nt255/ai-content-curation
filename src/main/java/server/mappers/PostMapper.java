package main.java.server.mappers;

import java.time.Instant;
import java.util.UUID;

import main.java.common.db.models.PostDbModel;
import main.java.server.models.post.CreatePostRequest;
import main.java.server.models.post.GetPostResponse;

public class PostMapper implements 
Mapper<GetPostResponse, CreatePostRequest, PostDbModel> {

    @Override
    public GetPostResponse mapFromDBModel(PostDbModel model) {
        return GetPostResponse.builder()
                .id(model.getId())
                .createdOn(model.getCreatedOn())
                .lastModifiedOn(model.getLastModifiedOn())
                .textJobId(model.getTextJobId())
                .imageJobId(model.getImageJobId())
                .build();
    }

    @Override
    public PostDbModel mapToDBModel(UUID generatedId, CreatePostRequest model) {
        return PostDbModel.builder()
                .id(generatedId)
                .createdOn(Instant.now())
                .lastModifiedOn(Instant.now())
                .textJobId(model.getTextJobId())
                .imageJobId(model.getImageJobId())
                .build();
    }

}
