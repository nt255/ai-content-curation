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
    
    private enum ImageType {
        PLAIN, UPSCALED
    }

    private ImageType type;
    
    private Integer height;
    private Integer width;
    private String checkpoint;
    private String workflow;

}
