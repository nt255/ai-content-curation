package main.java.server.models.post;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.server.models.BaseCreateRequest;

@Getter
@Setter
@SuperBuilder
public class CreatePostRequest extends BaseCreateRequest {
        
    // input
    private UUID textJobId;
    private UUID imageJobId;

}
