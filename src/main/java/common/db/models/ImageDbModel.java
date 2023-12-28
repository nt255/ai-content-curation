package main.java.common.db.models;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.image.ImageParams;
import main.java.common.models.image.ImageParamsType;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ImageDbModel extends JobDbModel {

    // input
    private UUID baseImageId;
    private List<ImageParamsType> steps;
    private List<ImageParams> params;
    
    // output
    private boolean isUpscaled;
    private String outputFilename;
    
}
