package main.java.common.models.image;

import java.util.List;

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
    
    private Integer height;
    private Integer width;
    private String checkpoint;
    private String workflow;
    
    private Integer kSteps;
    private Integer kCFG;
    private Double kNoise;
    
    private Integer upscaleSteps;
    private Integer upscaleCFG;
    private Double upscaleNoise;
    
    private List<LoraSettings> loraSettings;
    
}
