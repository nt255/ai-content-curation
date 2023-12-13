package main.java.common.db.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.enums.ImageType;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ImageDbModel extends JobDbModel {

    private ImageType type;
    
    private Integer height;
    private Integer width;
    private String checkpoint;
    private String workflow;
    
    private String outputFilename;
    
}
