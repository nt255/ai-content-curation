package main.java.server.models.image;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.image.ImageParams;
import main.java.common.models.image.ImageParamsType;
import main.java.server.models.GetJobResponse;

@Getter
@SuperBuilder
public class GetImageResponse extends GetJobResponse {

    // input
    private final UUID baseImageId;
    private final List<ImageParamsType> steps;
    private final List<ImageParams> params;

    // output
    private final String outputFilename;

}
