package main.java.common.models.image;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.BaseParams;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ImageParams extends BaseParams {
    
    private ImageParamsType type;
    
    // create new
    private Integer height;
    private Integer width;
    private String checkpoint;
    private String workflow;
    
    // generate
    private Integer kSteps;
    private Integer kCFG;
    
}
