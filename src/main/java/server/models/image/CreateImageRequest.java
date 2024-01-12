package main.java.server.models.image;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.image.ImageParams;
import main.java.server.models.BaseCreateRequest;

@Getter
@Setter
@SuperBuilder
public class CreateImageRequest extends BaseCreateRequest {
        
    // input
    private final UUID baseImageId;
    private final List<ImageParams> params;
    
    private final boolean overwrite;

}
