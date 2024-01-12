package main.java.server.models.post;

import java.util.UUID;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import main.java.server.models.BaseGetResponse;

@Getter
@SuperBuilder
public class GetPostResponse extends BaseGetResponse {

    // input
    private UUID textJobId;
    private UUID imageJobId;

}
