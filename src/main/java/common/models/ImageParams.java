package main.java.common.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ImageParams extends BaseParams {
    
    public enum ImageType {
        PLAIN, UPSCALED, FACE_DETAILED
    }

    private ImageType type;
    
    private Integer height;
    private Integer width;
    private String checkpoint;
    private String workflow;
    private Integer kSteps;
    private Integer kCFG;
    
    // bad practice?
    private Integer upscaleSteps;
    private Integer upscaleCFG;
    private String imagePath;
}
